package com.ruoyi.quartz.giftcard.mail;

import java.util.*;
import java.util.regex.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MailParser {

    public static List<String> extractGiftCardCodes(String text) {
        Set<String> codes = new LinkedHashSet<>(); // 保持顺序 + 去重
        Pattern pattern = Pattern.compile("Gift\\s*Card\\s*Code\\s*:\\s*([A-Z0-9\\-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            codes.add(matcher.group(1).trim());
        }
        return new ArrayList<>(codes);
    }

    public static List<String> extractOrderNumbers(String text) {
        Set<String> orders = new LinkedHashSet<>();
        Pattern pattern = Pattern.compile("Order\\s*Number\\s*:\\s*([\\d\\-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            orders.add(matcher.group(1).trim());
        }
        return new ArrayList<>(orders);
    }

    public static List<String> extractAmounts(String text) {
        Set<String> amounts = new LinkedHashSet<>();
        Pattern pattern = Pattern.compile("₹\\s*\\d+[.,]?\\d*");
        Matcher matcher = pattern.matcher(text);
        int index = 0;
        while (matcher.find()) {
            String amt = matcher.group().replaceAll("[^\\d.]", "");
            if (index % 2 == 0) { // 保留偶数索引
                amounts.add(amt);
            }
            index++;
        }
        return new ArrayList<>(amounts);
    }

    public static List<String> extractTypes(String html) {
        Set<String> types = new LinkedHashSet<>();
        Document doc = Jsoup.parse(html);
        Elements fonts = doc.select("font[style~=font-size:18px]");
        for (Element font : fonts) {
            types.add(font.text().trim());
        }
        return new ArrayList<>(types);
    }

    public static List<String> extractExtraNumbers(String html) {
        Set<String> numbers = new LinkedHashSet<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.select("div[style~=text-align:center.*font-size:20px]");
        for (Element div : divs) {
            numbers.add(div.text().trim());
        }
        return new ArrayList<>(numbers);
    }
}


