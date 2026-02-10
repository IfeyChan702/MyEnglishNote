//package com.ruoyi.system.quartz.giftcard.mailparse;
//
//import cn.kyber.payplat.framework.common.springevent.SpringEventPusher;
//import cn.kyber.payplat.framework.common.util.json.JsonUtils;
//import cn.kyber.payplat.module.plt.config.ThreadWatchDog;
//import cn.kyber.payplat.module.plt.service.email.mailevt.EmailEvt;
//import cn.kyber.payplat.module.plt.service.email.mailevt.EmailStorage;
//import cn.kyber.payplat.module.plt.service.email.mailevt.MailCompensateEvt;
//import cn.kyber.payplat.module.plt.service.email.mailevt.MailMessageDTOStorage;
//import cn.kyber.payplat.module.plt.utils.AESEncryptionUtil;
//import cn.kyber.payplat.module.plt.utils.NumberUtil;
//import com.sun.mail.imap.IMAPFolder;
//import jakarta.mail.*;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RBucket;
//import org.redisson.api.RedissonClient;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.util.NumberUtils;
//import org.springframework.util.StringUtils;
//
//import java.util.Properties;
//
//@Slf4j
//@Service
//public class EmaiProcessService {
//    @Autowired
//    private RedissonClient redissonClient;
//    @EventListener
//    @Async("emailExecutor")
//    public void asyncProcessEmail(EmailEvt emailEvt) {
//        Message msg = (Message) emailEvt.getSource();
//        String emailUid= emailEvt.getEmailUUID();
//        if (StringUtils.hasText(emailUid)){
//            MDC.put("traceId", emailUid);
//        }
//        EmaiMessagelReaderNew reader = new EmaiMessagelReaderNew();
//        try {
//            EmailMessageDTO emailMessageDTO = reader.searchIdFromEmail(msg);
//            log.info("📧 邮件内容解析完成: {}", emailMessageDTO);
//            if (emailMessageDTO != null) {
//                String presetOrderId = emailMessageDTO.getPresetOrderID();
//                int orderCount= EmailStorage.get(Long.valueOf(presetOrderId));
//                if (orderCount==-1||orderCount>1){
//                    MailCompensateEvt mailCompensateEvt = new MailCompensateEvt(emailMessageDTO);
//                    mailCompensateEvt.setEmailUUID(emailUid);
//                    SpringEventPusher.push(mailCompensateEvt);
//                }
//                if(orderCount!=-1){
//                    if (NumberUtil.isLong(presetOrderId)){
//                        MailMessageDTOStorage.put(Long.valueOf(presetOrderId), emailMessageDTO);
//                    }
//                    boolean unPark = ThreadWatchDog.wakeup(presetOrderId);
//                }
//
//            }else {
//                log.info("没有提取到ID信息 ({})", msg.getSubject());
//            }
//        }catch (Exception e){
//            try {
//                log.error("处理邮件失败 ({})", msg.getSubject(), e);
//            }catch (MessagingException ex){
//                log.error("邮件处理器异常失败", ex);
//            }
//        }finally {
//            MDC.remove("traceId");
//        }
//
//    }
//
//    public boolean  checkEmailIsAVailable(String username, String password) {
//        // 创建一个 Session 对象
//        String host = "imap.gmail.com";
//        if (!username.contains("@gmail")){
//            host = "imappro.zoho.com";
//        }
//        int port = 993;
//        Properties props = new Properties();
//        props.put("mail.imap.ssl.enable", "true");
//        props.put("mail.imap.host", host);
//        props.put("mail.imap.port", port);
//        Session session = Session.getInstance(props, null);
//        try {
//            // 1. 连接 IMAP 服务器
//            Store store = session.getStore("imaps");
//            store.connect(host, username, AESEncryptionUtil.decrypt(password));
//
//            // 2. 打开收件箱（必须以 READ_WRITE 模式才能获取 UID）
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_ONLY); // READ_ONLY 足够用于读取
//
//            // 3. 获取 IMAPFolder 以支持 UID 操作
//            if (!(inbox instanceof IMAPFolder)) {
//                throw new RuntimeException("Not an IMAP folder");
//            }
//            // 4. 关闭资源
//            inbox.close(false);
//            store.close();
//            return true;
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//}
