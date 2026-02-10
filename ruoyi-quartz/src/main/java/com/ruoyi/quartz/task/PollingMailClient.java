package com.ruoyi.quartz.task;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.quartz.giftcard.mail.MailParser;
import com.ruoyi.quartz.giftcard.mailevt.EmailEvt;
import com.ruoyi.quartz.giftcard.mailparse.MailAccount;
import com.ruoyi.system.controller.GiftCardController;
import com.ruoyi.system.controller.openapi.InsertGiftCard;
import com.ruoyi.system.domain.GiftCard;
import com.sun.mail.imap.IMAPFolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Component
@Data
public class PollingMailClient {
    @Autowired
    private RedisCache redisCache;

    private static final String LAST_UID_KEY = "mail:lastUid";
    public final MailAccount account;
    public Store store;
    public Folder folder;
    private boolean connected = false;

    private int consecutiveFailures = 0;
    private static final int MAX_FAILURES = 3;
    private volatile boolean suspended = false;

    @Autowired
    GiftCardController giftCardController;
    public PollingMailClient() {
        this.account = new MailAccount();
        this.account.setEmail("my8981463050@gmail.com");
        this.account.setPassword("gwazetbfqoddeins"); // 建议改成配置文件读取
        this.account.setImapPort(993);
        this.account.setImapHost("imap.gmail.com");
        this.account.setSslEnabled(true);
    }

    public void saveLastUid(long lastUid) {
        // 存到 Redis
        redisCache.setCacheObject(LAST_UID_KEY, lastUid);
    }

    public long getLastUid() {
        // 从 Redis 取出
        Long uid = redisCache.getCacheObject(LAST_UID_KEY);
        return uid != null ? uid : 0L;
    }

//    @PostConstruct
    public void poll() {
        if (suspended) {
            return;
        }
        try {
            ensureConnected();
            folder.getMessageCount(); // 触发连接验证
            long currentMaxUid = getCurrentMaxUid();
            if (currentMaxUid > getLastUid()) {
                log.info("📧 有新的卡密邮件 [{}]: currentMaxUid={}, getLastUid()={}", getEmail(), currentMaxUid, getLastUid());
                fetchAndPublishMessages(getLastUid() + 1, currentMaxUid);
                saveLastUid(currentMaxUid);
            }

            if (consecutiveFailures > 0) {
                log.info("🔄 邮箱 {} 恢复正常，清除失败计数", getEmail());
                consecutiveFailures = 0;
            }

        } catch (Exception e) {
            consecutiveFailures++;
            log.warn("❌ 邮箱 {} 轮询失败 (第 {}/{} 次)", getEmail(), consecutiveFailures, MAX_FAILURES, e);

//            close();

            if (consecutiveFailures >= MAX_FAILURES) {
                suspended = true;
                log.error("🛑 邮箱 {} 连续失败 {} 次，已自动挂起轮询！", getEmail(), MAX_FAILURES);
            }
        }
    }

    private void ensureConnected() {
        if (!connected) {
            connect();
        }
    }

    private void connect() {
        close();
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props);
        try {
            store = session.getStore("imaps");
            store.connect(account.getImapHost(), account.getEmail(), account.getPassword());
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            if (getLastUid() == 0) {
                saveLastUid(getCurrentMaxUid());
                log.info("✅ 邮箱 {} 连接成功，初始最大 UID: {}", getEmail(), getLastUid());
            } else {
                log.info("✅ 邮箱 {} 恢复连接，最大 UID: {}", getEmail(), getLastUid());
            }
            connected = true;
        } catch (Exception e) {
            log.error("❌ 邮箱 {} 连接失败", getEmail(), e);
        }
    }

    private long getCurrentMaxUid() {
        try {
            if (folder.getMessageCount() == 0){
                return 0;
            }
            Message last = folder.getMessage(folder.getMessageCount());
            return ((IMAPFolder) folder).getUID(last);
        } catch (Exception e) {
            log.error("❌ 邮箱 {} 获取最大 UID 失败", getEmail(), e);
            return getLastUid();
        }
    }

    /**
     * 批量处理卡密
     * @param startUid
     * @param endUid
     */
    private void fetchAndPublishMessages(long startUid, long endUid) {
        try {
            Message[] messages = ((IMAPFolder) folder).getMessagesByUID(startUid, endUid);
            for (Message msg : messages) {
                try {
                    long uid = ((IMAPFolder) folder).getUID(msg);
                    String traceId = UUID.randomUUID().toString();
                    MDC.put("traceId", traceId);
                    String subject = safeGetSubject(msg);
                    String content = getTextFromMessage(msg);
                    Address[] froms = msg.getFrom();
                    String from = (froms != null && froms.length > 0) ? froms[0].toString() : null; //发件人。
                    List<String> amounts = MailParser.extractAmounts(content);
                    List<String> giftCardCodes = MailParser.extractGiftCardCodes(content);
                    List<String> orderNumbers = MailParser.extractOrderNumbers(content);//亚马逊订单号
                    List<String> types = MailParser.extractTypes(content);  //Amazon Pay Gift Card 订单类型0
                    List<String> numbers = MailParser.extractExtraNumbers(content);
                    for (int i = 0; i < giftCardCodes.size(); i++) {
                        try {
                            String gcCode = giftCardCodes.get(i);
                            String amount = (i < amounts.size()) ? amounts.get(i) : "0";
                            String orderNumber = (i < orderNumbers.size()) ? orderNumbers.get(i) : "未知订单号";
                            String type = (i < types.size()) ? types.get(i) : "未知类型";
                            String number = (i < numbers.size()) ? numbers.get(i) : "未知编号";
                            if (type.toLowerCase().contains("amazon")) {
                                type = "0";
                            }
                            if (type.toLowerCase().contains("apple")) {
                                type = "1";
                            }
                            if (type.toLowerCase().contains("google")) {
                                type = "2";
                            }

                            GiftCard giftCard = new GiftCard();
                            giftCard.setAmount((long) Double.parseDouble(amount));
                            giftCard.setCode(gcCode);
                            giftCard.setGiftType(type);
                            giftCard.setExtraNumber(number);
                            giftCard.setOrderNumber(orderNumber);
                            giftCard.setSender(from);
                            giftCard.setSubject(subject);
                            giftCard.setStatus(0L);
                            giftCard.setUsageType("-1");
                            giftCard.setCreateTime(msg.getSentDate());
                            giftCard.setDtStr(msg.getSentDate().toString());
                            giftCardController.addOnSystem(giftCard);
                        }catch (Exception e){
                            log.error("❌ 邮箱 {} 处理卡密失败: {}", getEmail(), e.getMessage());

                        }
                    }
                    log.info("📧 新邮件 [{}]: UID={}, 主题={}", getEmail(), uid, subject);
                    EmailEvt event = new EmailEvt(msg);
                    event.setEmailUUID(traceId);
                    MDC.remove("traceId");
                }catch (Exception e){
                    log.error("❌ 邮箱 {} 处理邮件失败", getEmail(), e);
                }
            }
        }catch (Exception e){
            log.error("❌ 邮箱 {} 获取邮件失败", getEmail(), e);
        }
    }
    private String getTextFromMessage(Message message){
        try {
            if (message.isMimeType("text/plain")) {
                return message.getContent().toString();
            } else if (message.isMimeType("text/html")) {
                return message.getContent().toString(); // HTML 内容
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                return
                        getTextFromMultipart(multipart);
            }
            return "";
        }catch (Exception e){
            return "";
        }
    }
    private String getTextFromMultipart(Multipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                // 如果你只想要纯文本，可以跳过 HTML
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof Multipart) {
                result.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }
    private String safeGetSubject(Message msg) {
        try {
            return msg.getSubject() == null ? "[无主题]" : msg.getSubject();
        } catch (Exception e) {
            return "[解析主题失败]";
        }
    }

    public synchronized void close() {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(false);
            } catch (Exception ignored) {
            }
        }
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (Exception ignored) {
            }
        }
        connected = false;
    }

    public void resetFailureCount() {
        this.consecutiveFailures = 0;
        this.suspended = false;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public String getEmail() {
        return account.getEmail();
    }
}
