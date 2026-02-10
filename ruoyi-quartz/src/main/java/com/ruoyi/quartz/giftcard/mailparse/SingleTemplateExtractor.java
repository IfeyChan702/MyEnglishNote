//package com.ruoyi.system.quartz.giftcard.mailparse;
//
//import jakarta.mail.BodyPart;
//import jakarta.mail.internet.MimeMultipart;
//
//public class SingleTemplateExtractor {
//
//    /**
//     * 极速版本 - HTML总是在第一部分
//     */
//    public static String extractUltraFast(MimeMultipart multipart) {
//        try {
//            // 最快路径：直接获取第一部分
//            if (multipart != null && multipart.getCount() > 0) {
//                BodyPart firstPart = multipart.getBodyPart(0);
//
//                // 快速检查Content-Type（可选，如果需要验证）
//                // String type = firstPart.getContentType();
//                // if (type != null && type.toLowerCase().contains("text/html")) {
//
//                Object content = firstPart.getContent();
//                if (content instanceof String) {
//                    String html = (String) content;
//                    // 实时处理：如果内容太大，安全截断
//                    return html.length() > 500000 ?
//                            html.substring(0, 500000) : html;
//                }
//                // }
//            }
//        } catch (Exception e) {
//            // 静默失败，返回空字符串
//            // 实时处理不允许抛出异常阻塞流程
//        }
//        return "";
//    }
//
//    /**
//     * 带快速验证的版本
//     */
//    public static String extractWithValidation(MimeMultipart multipart) {
//        try {
//            if (multipart == null) return "";
//
//            int count = multipart.getCount();
//            if (count == 0) return "";
//
//            // 检查第一部分是否为HTML
//            BodyPart firstPart = multipart.getBodyPart(0);
//            String contentType = firstPart.getContentType();
//
//            if (contentType != null) {
//                // 快速检查是否是HTML（不调用toLowerCase）
//                if (isHtmlContentType(contentType)) {
//                    Object content = firstPart.getContent();
//                    if (content instanceof String) {
//                        return (String) content;
//                    }
//                }
//            }
//
//            // 后备：如果没有Content-Type或不是HTML，仍然尝试获取
//            Object content = firstPart.getContent();
//            if (content instanceof String) {
//                return (String) content;
//            }
//
//        } catch (Exception e) {
//            // 记录错误但不抛出
//            System.err.println("Extraction error: " + e.getClass().getSimpleName());
//        }
//        return "";
//    }
//
//    /**
//     * 优化的HTML Content-Type检查
//     * 避免toLowerCase()和contains()的开销
//     */
//    private static boolean isHtmlContentType(String contentType) {
//        // 长度检查
//        if (contentType.length() < 9) return false; // "text/html"的最小长度
//
//        // 手动检查"text/html"，不区分大小写
//        char c0 = contentType.charAt(0);
//        char c1 = contentType.charAt(1);
//        char c2 = contentType.charAt(2);
//        char c3 = contentType.charAt(3);
//
//        // 检查"text"
//        if (!(c0 == 't' || c0 == 'T') ||
//                !(c1 == 'e' || c1 == 'E') ||
//                !(c2 == 'x' || c2 == 'X') ||
//                !(c3 == 't' || c3 == 'T')) {
//            return false;
//        }
//
//        // 检查'/'
//        if (contentType.charAt(4) != '/') return false;
//
//        // 检查"html"
//        char c5 = contentType.charAt(5);
//        char c6 = contentType.charAt(6);
//        char c7 = contentType.charAt(7);
//        char c8 = contentType.charAt(8);
//
//        return (c5 == 'h' || c5 == 'H') &&
//                (c6 == 't' || c6 == 'T') &&
//                (c7 == 'm' || c7 == 'M') &&
//                (c8 == 'l' || c8 == 'L');
//    }
//}
