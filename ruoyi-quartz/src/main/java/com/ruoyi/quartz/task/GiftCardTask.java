package com.ruoyi.quartz.task;

import com.ruoyi.system.mapper.GiftCardMapper;
import com.ruoyi.system.service.IGiftCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.common.constant.CacheConstants.GIFT_CARD_PRE_OCCUPY_PREFIX;
import static com.ruoyi.common.constant.TimeConstants.PRE_OCCUPY_TIMEOUT_MS;

/**
 * @author boyo
 */
@Slf4j
@Component("giftCardTask")
public class GiftCardTask {


    @Autowired
    private GiftCardMapper giftCardMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void processPendingGiftCards() {
        log.info("【礼品卡超时释放任务】开始执行");

        final String prefix = GIFT_CARD_PRE_OCCUPY_PREFIX + ":";

        Set<String> expiredKeys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();

            ScanOptions options = ScanOptions.scanOptions()
                    .match(prefix + "*")
                    .count(1000)
                    .build();

            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes, StandardCharsets.UTF_8);

                    byte[] valueBytes = connection.get(keyBytes);
                    if (valueBytes == null) {
                        keys.add(key);
                        continue;
                    }

                    String value = new String(valueBytes, StandardCharsets.UTF_8);
                    try {
                        long ts = Long.parseLong(value);
                        long diff = System.currentTimeMillis() - ts;
                        if (diff > PRE_OCCUPY_TIMEOUT_MS) {
                            keys.add(key);
                        }
                    } catch (NumberFormatException e) {
                        // value 不是时间戳，属于脏数据，也一起清掉
                        log.warn("【礼品卡超时释放任务】key [{}] 的值不是合法时间戳: {}", key, value);
                        keys.add(key);
                    }
                }
            } catch (Exception e) {
                log.error("【礼品卡超时释放任务】扫描 Redis 预占用 key 失败", e);
            }

            return keys;
        });

        if (expiredKeys == null || expiredKeys.isEmpty()) {
            log.info("【礼品卡超时释放任务】无超时卡");
            return;
        }

        List<Long> cardIds = expiredKeys.stream()
                .map(key -> {
                    try {
                        String idStr = key.substring(prefix.length());
                        return Long.parseLong(idStr);
                    } catch (Exception e) {
                        log.warn("【礼品卡超时释放任务】无法从 key [{}] 解析礼品卡 id", key, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (cardIds.isEmpty()) {
            log.info("【礼品卡超时释放任务】无有效卡 ID 需要释放，仅清理 Redis key");
            redisTemplate.delete(expiredKeys);
            return;
        }

        int count = giftCardMapper.batchResetToUnused(cardIds);

        redisTemplate.delete(expiredKeys);

        log.info("【礼品卡超时释放任务】完成，释放 {} 张卡，对应 Redis key 已删除 {}", count, expiredKeys.size());
    }

}
