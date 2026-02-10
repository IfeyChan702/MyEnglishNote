//package com.ruoyi.system.quartz.giftcard;
//
//import jakarta.mail.Folder;
//import jakarta.mail.Message;
//import jakarta.mail.Session;
//import jakarta.mail.Store;
//import jakarta.mail.internet.InternetAddress;
//import lombok.Data;
//import org.springframework.stereotype.Component;
//import com.ruoyi.system.quartz.giftcard.mailparse.MailAccount;
//
//import javax.annotation.PostConstruct;
//import java.util.Properties;
//
//@Component
//@Data
//public class MyMailSchedule {
//    MailAccount account;
//    volatile boolean suspended = false;
//    boolean connected = false;
//    public MailAccount getAcount() {
//        if (account == null) {
//            this.account = new MailAccount();
//            this.account.setEmail("my8981463050@gmail.com");
//            this.account.setPassword("gwazetbfqoddeins"); // 建议改成配置文件读取
//            this.account.setImapPort(993);
//            this.account.setImapHost("imap.gmail.com");
//            this.account.setSslEnabled(true);
//        }
//        return account;
//    }
//
//    /**
//     * 获取邮件的主要方法。
//     */
//    @PostConstruct
//    public void ScheduleMain(){
//        try {
//            // 配置属性
//            Properties props = new Properties();
//            props.setProperty("mail.store.protocol", "imaps");
//
//            // 创建会话
//            Session session = Session.getInstance(props);
//
//            // 连接到 Gmail
//            Store store = session.getStore("imaps");
//            store.connect("imap.gmail.com", "my8981463050@gmail.com", "gwazetbfqoddeins");
//
//            // 打开收件箱
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_ONLY);
//
//            // 获取最近10封邮件
//            Message[] messages = inbox.getMessages(inbox.getMessageCount() - 9, inbox.getMessageCount());
//            for (Message msg : messages) {
//                System.out.println("主题: " + msg.getSubject());
//                System.out.println("发件人: " + InternetAddress.toString(msg.getFrom()));
//                System.out.println("发送时间: " + msg.getSentDate());
//                System.out.println("-----------------------------");
//            }
//
//            // 关闭连接
//            inbox.close(false);
//            store.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
////        if (suspended) {
////            return;
////        }
////        ensureConnected();
//    }
//
//        private void ensureConnected() {
//        if (!connected) {
//            connect();
//        }
//    }
//    private void connect() {
////        close();
//        Properties props = new Properties();
//        props.setProperty("mail.store.protocol", "imaps");
//        Session session = Session.getInstance(props);
//        try {
////            store = session.getStore("imaps");
////                store.connect(account.getImapHost(), account.getEmail(), account.getPassword());
////
////        folder = store.getFolder("INBOX");
////        folder.open(Folder.READ_ONLY);
////            if (lastUid == 0) {
////                lastUid = getCurrentMaxUid();
////                log.info("✅ 邮箱 {} 连接成功，初始最大 UID: {}", getEmail(), lastUid);
////            } else {
////                log.info("✅ 邮箱 {} 恢复连接，最大 UID: {}", getEmail(), lastUid);
////            }
//            connected = true;
//        } catch (Exception e) {
////            log.error("❌ 邮箱 {} 连接失败", getEmail(), e);
//        }
//    }
//}
