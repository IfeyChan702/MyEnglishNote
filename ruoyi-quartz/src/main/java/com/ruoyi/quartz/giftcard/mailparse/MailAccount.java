package com.ruoyi.quartz.giftcard.mailparse;

import lombok.Data;
@Data
public class MailAccount {
    private String email;           // 邮箱地址
    private String password;        // 应用密码
    private String imapHost;        // 如 imap.gmail.com
    private Integer imapPort;       // 如 993
    private Boolean sslEnabled;     // true
}
