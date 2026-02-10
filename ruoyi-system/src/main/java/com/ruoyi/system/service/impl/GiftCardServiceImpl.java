package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.vo.GiftCardQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.GiftCardMapper;
import com.ruoyi.system.domain.GiftCard;
import com.ruoyi.system.service.IGiftCardService;
import org.springframework.transaction.annotation.Transactional;


import static com.ruoyi.common.constant.CacheConstants.GIFT_CARD_PRE_OCCUPY_PREFIX;
import static com.ruoyi.common.constant.TimeConstants.GIFT_CARD_PRE_OCCUPY_TTL;

/**
 * 礼品卡Service业务层处理
 *
 * @author ifey
 * @date 2025-12-02
 */
@Service
public class GiftCardServiceImpl implements IGiftCardService
{
    @Autowired
    private GiftCardMapper giftCardMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String USERINFO_LOCK = "redis:giftcard:lock:code:";
    /**
     * 查询礼品卡
     *
     * @param id 礼品卡主键
     * @return 礼品卡
     */
    @Override
    public GiftCard selectGiftCardById(Long id)
    {
        return giftCardMapper.selectGiftCardById(id);
    }

    /**
     * 查询礼品卡列表
     *
     * @param giftCard 礼品卡
     * @return 礼品卡
     */
    @Override
    public List<GiftCard> selectGiftCardList(GiftCard giftCard)
    {
        return giftCardMapper.selectGiftCardList(giftCard);
    }

    @Override
    public List<GiftCard> selectGiftCardList(GiftCardQueryVo queryVo) {


        return giftCardMapper.selectGiftCardListByTime(queryVo);
    }

    /**
     * 新增礼品卡
     *
     * @param giftCard 礼品卡
     * @return 结果
     */
    @Override
    public int insertGiftCard(GiftCard giftCard)
    {
        giftCard.setCreateTime(DateUtils.getNowDate());
        return giftCardMapper.insertGiftCard(giftCard);
    }

    /**
     * 修改礼品卡
     *
     * @param giftCard 礼品卡
     * @return 结果
     */
    @Override
    public int updateGiftCard(GiftCard giftCard)
    {
        return giftCardMapper.updateGiftCard(giftCard);
    }

    /**
     * 批量删除礼品卡
     *
     * @param ids 需要删除的礼品卡主键
     * @return 结果
     */
    @Override
    public int deleteGiftCardByIds(Long[] ids)
    {
        return giftCardMapper.deleteGiftCardByIds(ids);
    }

    /**
     * 删除礼品卡信息
     *
     * @param id 礼品卡主键
     * @return 结果
     */
    @Override
    public int deleteGiftCardById(Long id)
    {
        return giftCardMapper.deleteGiftCardById(id);
    }

    @Override
    public int updateGiftCardByCode(GiftCard giftCard) {
        return giftCardMapper.updateGiftCardByCode(giftCard);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GiftCard getOldestGiftCardByStatus(Long status,String giftType) {

        GiftCard giftCard = giftCardMapper.selectOldestGiftCardByStatusAndTypeForUpdate(status, giftType);
        if (giftCard == null) {
            return null;
        }

        String lockKey = GIFT_CARD_PRE_OCCUPY_PREFIX + ":" + giftCard.getId();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))){
            return null;
        }

        giftCard.setStatus(3L);
        giftCardMapper.updateGiftCard(giftCard);

        redisTemplate.opsForValue().set(
                lockKey,
                String.valueOf(System.currentTimeMillis()),
                GIFT_CARD_PRE_OCCUPY_TTL,
                TimeUnit.SECONDS
        );

        return giftCard;
    }

    @Override
    public Double selectGiftCardAmountSum(GiftCardQueryVo queryVo) {
        return giftCardMapper.selectGiftCardAmountSum(queryVo);
    }
}
