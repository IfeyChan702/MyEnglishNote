//package com.ruoyi.system.quartz.giftcard.mailparse;
//
//import cn.kyber.payplat.framework.common.util.cache.LocalCacheUtil;
//import cn.kyber.payplat.module.plt.config.SysCache;
//import cn.kyber.payplat.module.plt.dal.dataobject.presetorder.PresetOrderDO;
//import cn.kyber.payplat.module.plt.dal.mysql.account.AccountMapper;
//import cn.kyber.payplat.module.plt.dal.mysql.presetorder.PresetOrderMapper;
//import cn.kyber.payplat.module.plt.service.email.mailevt.EmailStorage;
//import cn.kyber.payplat.module.plt.utils.NumberUtil;
//import cn.kyber.payplat.module.plt.utils.SpringUtil;
//import com.sun.mail.imap.IMAPFolder;
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMultipart;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.lucene.analysis.core.WhitespaceTokenizer;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.redisson.api.RedissonClient;
//import org.springframework.util.StringUtils;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.StringReader;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class EmaiMessagelReaderNew {
//    //获取静态的redission客户端
//    private static RedissonClient redissonClient = null;
//    private static AccountMapper accountMapper;
//    private static PresetOrderMapper presetOrderMapper;
//
//    private static final List<String> amazonEmails =new ArrayList<>( Arrays.asList(
//            "no-reply@amazon.com",
//            "no-reply@amazonpay.in",
//            "giftcards@amazon.com",
//            "giftcards@amazon.in",
//            "cs-reply@amazon.com"
//    ));
//
//    public EmailMessageDTO searchIdFromEmail(Message message) throws Exception {
//        //获取邮件主题
//        String subject = message.getSubject();
//        log.info("📧 邮件主题: {} --> 开始解析", subject);
//        //获取邮件ipaddr 地址
//        String ipaddr =new InternetAddress(message.getFrom()[0].toString()).getAddress();
//        //发件时间
//        Date sentDate = message.getSentDate();
//        LocalDateTime sentTime = sentDate.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//        //摘要
//        String summary = subject;
//
//        //筛选邮件
//        if (!amazonEmails.contains(ipaddr)||!subject.toLowerCase().contains("gift card")) {
//            log.info("过滤 邮件 主题：{} 邮件ipaddr：{} 邮件内容：{} 邮件发送时间：{}",subject,ipaddr,summary,sentTime);
//            return null;
//        }
//
//        String html = getEmailBody(message);
//        //邮件内容 实际负载操作 耗时操作
//        String emailBody = parseEmailBodyToString(html);
//        if (!StringUtils.hasText(emailBody)){
//            log.info("未拿到邮件内容 -> 过滤 邮件 邮件ipaddr：{} 邮件内容：{} 邮件发送时间：{}",ipaddr,summary,sentTime);
//            return null;
//        }
//        //记录接收到邮件的时间
//        LocalDateTime receivedTime = LocalDateTime.now();
//        //对邮件内容进行分词
//        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
//        tokenizer.setReader(new StringReader(emailBody));
//        CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);
//        tokenizer.reset();
//        List<String> tokens = new ArrayList<>();
//        Long presetOrderID=null;
//        while (tokenizer.incrementToken()) {
//            String token = term.toString();
//            // 跳过纯标点符号（不包含字母、数字、连字符等）
//            if (!NumberUtil.isPurePunctuation( token)) {
//                if (NumberUtil.isIntInRange(token)){
//                    int orderCount=EmailStorage.get(Long.parseLong(token));
//                    if (orderCount>0||orderCount==-1)presetOrderID=Long.parseLong(token);
//                }
//                tokens.add(token);
//            }
//        }
//        tokenizer.end();
//        tokenizer.close();
//        if (presetOrderID==null)return null;
//        String amazonOrderID = getAmazonOrderID(tokens);
//        if (!StringUtils.hasText(amazonOrderID)) {
//            log.error("预单号：{} 未能正确定位 亚马逊单号请检查邮件： \n{} \n{} \n{}",presetOrderID,summary,subject,sentTime);
//            return null;
//        }
//        //记录解析完成的时间
//        LocalDateTime parsedTime = LocalDateTime.now();
//        EmailMessageDTO emailMessageDTO = new EmailMessageDTO(subject,ipaddr,sentTime,summary,presetOrderID+"",amazonOrderID);
//        emailMessageDTO.setAmazonMailTime(receivedTime);
//        emailMessageDTO.setAmazonParseTime(parsedTime);
//        return emailMessageDTO;
//    }
//
//
//
//    /**
//     * 提取邮件正文（简单处理 text/plain）
//     */
//    private String getEmailBody(Message message) throws Exception {
//        if (message.isMimeType("text/plain")) {
//            return message.getContent().toString();
//        } else if (message.isMimeType("text/html")) {
//            return message.getContent().toString(); // 或使用 Jsoup 清理 HTML
//        } else if (message.isMimeType("multipart/*")) {
//            MimeMultipart multipart = (MimeMultipart) message.getContent();
//            return getTextFromMimeMultipart(multipart);
//            //return SingleTemplateExtractor.extractUltraFast(multipart);
//        }
//        return "[无法解析正文]";
//    }
//
//    /**
//     * 获取亚马逊订单号
//     */
//    private String getAmazonOrderID(List<String> tokens) {
//        for (int i = 0; i < tokens.size(); i++) {
//            if (tokens.get(i).equals("Order")&&tokens.get(i+1).equals("Number")){
//                return tokens.get(i+2);
//            }
//        }
//        return null;
//
//    }
//
//
//    /**
//     * 解析邮件内容
//     *
//     * @param html 邮件内容
//     * @return 解析结果
//     */
//    @Deprecated
//    private String parseEmailBodyToString(String html) {
//        // 示例解析逻辑，根据实际邮件格式调整
//        html=(html.replace("\r","").replace("\n",""));
//        Document doc = Jsoup.parse(html);
//        Elements elements = doc.getElementsByTag("table");
//        String bodyText = null;
//        for (Element elet : elements) {
//            if (elet.hasText() && (elet.text().contains("Brand Gift Card Details") || elet.text().contains("How to use Gift Card?"))) {
//                bodyText = elet.text();
//                break;
//            }
//        }
//        return bodyText;
//    }
//
//
//
//
//    private static String getTextFromPart(BodyPart part)
//            throws MessagingException, IOException {
//        Object content = part.getContent();
//        if (content instanceof String) {
//            return (String) content;
//        } else if (content instanceof InputStream) {
//            return inputStreamToString((InputStream) content);
//        }
//        return content != null ? content.toString() : "";
//    }
//
//    private static String inputStreamToString(InputStream inputStream) throws IOException {
//        Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
//        return s.hasNext() ? s.next() : "";
//    }
//
//
//    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
//        if (mimeMultipart == null) return "";
//
//        int count = mimeMultipart.getCount();
//
//        // 假设80%的情况HTML在前3个部分
//        int htmlSearchLimit = Math.min(count, 3);
//        for (int i = 0; i < htmlSearchLimit; i++) {
//            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
//            if (bodyPart.isMimeType("text/html")) {
//                Object content = bodyPart.getContent();
//                if (content instanceof String) {
//                    String html = (String) content;
//                    if (!html.isEmpty()) return html;
//                }
//            }
//        }
//
//        // 如果没找到HTML，搜索所有部分的纯文本
//        for (int i = 0; i < count; i++) {
//            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
//            if (bodyPart.isMimeType("text/plain")) {
//                Object content = bodyPart.getContent();
//                if (content instanceof String) {
//                    String text = (String) content;
//                    if (!text.isEmpty()) return text;
//                }
//            }
//        }
//
//        return "";
//    }
//
//    public static void main(String[] args) {
//        // 邮箱账号配置
//        String host = "imap.gmail.com";
//        int port = 993;
//        String username = "yuc74302@gmail.com";
//        String password = "oeqpbxvmofehllum"; // 应用专用密码
//        // 要拉取的邮件 UID（示例）
//        long targetUid = 1612; // ←←← 替换为你想拉取的实际 UID
//
//        EmailStorage.put(24409L,1);
//
//      /*  host="imappro.zoho.com";
//        username="amit@bbycard.com";
//        password="Shijian_2025";
//        EmailStorage.put(22317L,1);
//        targetUid = 4; // ←←← 替换为你想拉取的实际 UID
//
//*/
//
//        Properties props = new Properties();
//        props.put("mail.imap.ssl.enable", "true");
//        props.put("mail.imap.host", host);
//        props.put("mail.imap.port", port);
//
//        //72.13.225.168:443:XqkO108662:GmpCzgHt
//
//        // 设置自定义 SocketFactory
//      /*  SSLSocketFactory factory = new ProxyAuthSSLSocketFactory(
//                "72.13.225.168",
//                443,
//                "XqkO108662",   // 新增字段
//                "GmpCzgHt"    // 新增字段
//        );
//        props.put("mail.imaps.ssl.socketFactory", factory);
//        props.put("mail.imaps.ssl.socketFactory.fallback", "false");
//        log.info("🔌 使用带认证的 HTTP 代理 {}:{}", "72.13.225.168", 443);*/
//
//
//        Set<Long> ids=(HashSet<Long>) LocalCacheUtil.cache().get(SysCache.UNHANDLED_PRESET_ORDER_IDS, t-> new HashSet<Long>());
//        ids.add(19440l);
//        ids.add(19441l);
//        ids.add(19442l);
//        List<String> accountDOs=List.of("AC01","AC02","AC03","AC04","AC05","AC06","AC07","AC08","AC09","AC10","AC11","AC12","AC13","AC14","AC15","AC16","AC17","AC18","AC19","AC20","AC21","AC22","AC23","AC24","AC25","AC26","AC27","AC28","AC29");
//        LocalCacheUtil.cache().put(SysCache.ACCOUNT_NAME_LIST,accountDOs);
//        LocalCacheUtil.cache().put(SysCache.UNHANDLED_PRESET_ORDER_IDS,ids);
//        Session session = Session.getInstance(props, null);
//        // 可选：开启调试日志
//        // session.setDebug(true);
//
//        try {
//            // 1. 连接 IMAP 服务器
//            Store store = session.getStore("imaps");
//            store.connect(host, username, password);
//
//            // 2. 打开收件箱（必须以 READ_WRITE 模式才能获取 UID）
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_ONLY); // READ_ONLY 足够用于读取
//
//            // 3. 获取 IMAPFolder 以支持 UID 操作
//            if (!(inbox instanceof IMAPFolder)) {
//                throw new RuntimeException("Not an IMAP folder");
//            }
//            IMAPFolder imapFolder = (IMAPFolder) inbox;
//
//            // 4. 通过 UID 获取邮件
//            Message msg = imapFolder.getMessageByUID(targetUid);
//            if (msg == null) {
//                System.out.println("No message found with UID: " + targetUid);
//                return;
//            }
//            System.out.println("Message: " + msg.getSubject());
//            //打印耗时
//            Long startTime = System.currentTimeMillis();
//            EmailMessageDTO emailMessageDTO =new EmaiMessagelReaderNew().searchIdFromEmail(msg);
//            log.info("邮件耗时: {}", System.currentTimeMillis() - startTime);
//            System.out.println(emailMessageDTO);
//
//            // 7. 关闭资源
//            inbox.close(false);
//            store.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //使用非递归版本的深度优先搜索
//    private static String getTextFromMimeMultipartIterative(MimeMultipart mimeMultipart) throws MessagingException, IOException {
//        if (mimeMultipart == null) return "";
//
//        Deque<MimeMultipart> stack = new ArrayDeque<>();
//        stack.push(mimeMultipart);
//
//        String html = null;
//        String text = null;
//
//        while (!stack.isEmpty() && html == null) {
//            MimeMultipart current = stack.pop();
//            int count = current.getCount();
//
//            for (int i = 0; i < count && html == null; i++) {
//                BodyPart bodyPart = current.getBodyPart(i);
//                String contentType = bodyPart.getContentType();
//
//                if (contentType != null) {
//                    String lowerContentType = contentType.toLowerCase();
//
//                    if (lowerContentType.contains("text/html")) {
//                        String content = getTextFromPart(bodyPart);
//                        if (content != null && !content.isEmpty()) {
//                            html = content;
//                        }
//                    } else if (lowerContentType.contains("text/plain") && text == null) {
//                        String content = getTextFromPart(bodyPart);
//                        if (content != null && !content.isEmpty()) {
//                            text = content;
//                        }
//                    } else if (lowerContentType.contains("multipart/")) {
//                        Object content = bodyPart.getContent();
//                        if (content instanceof MimeMultipart) {
//                            stack.push((MimeMultipart) content);
//                        }
//                    }
//                }
//            }
//        }
//
//        return html != null ? html : (text != null ? text : "");
//    }
//
//}
