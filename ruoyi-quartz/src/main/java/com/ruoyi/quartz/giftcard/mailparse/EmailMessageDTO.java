package com.ruoyi.quartz.giftcard.mailparse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EmailMessageDTO {
    private String subject;
    private String from;
    private LocalDateTime sentDate;
    private String summary;
    private String presetOrderID;
    private String amazonOrderNO;
    /**
     * 接收到亚马逊邮件时间
     */
    private LocalDateTime amazonMailTime;
    /**
     * 亚马逊邮件解析完成时间
     */
    private LocalDateTime amazonParseTime;
    //构造
    public EmailMessageDTO(String subject, String from, LocalDateTime sentDate, String summary, String presetOrderID, String amazonOrderNO) {
        this.subject = subject;
        this.from = from;
        this.sentDate = sentDate;
        this.summary = summary;
        this.presetOrderID = presetOrderID;
        this.amazonOrderNO = amazonOrderNO;
    }
    //json to string
    @Override
    public String toString() {
        // json
        return "{" +
                "\"subject\":\"" + subject + "\"," +
                "\"from\":\"" + from + "\"," +
                "\"sentDate\":\"" + sentDate + "\"," +
                "\"summary\":\"" + summary + "\"," +
                "\"presetOrderID\":\"" + presetOrderID + "\"," +
                "\"amazonOrderNO\":\"" + amazonOrderNO + "\"," +
                "\"amazonMailTime\":\"" + amazonMailTime + "\"," +
                "\"amazonParseTime\":\"" + amazonParseTime + "\"" +
                "}";
    }

}
