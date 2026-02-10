package com.ruoyi.quartz.giftcard.mailevt;

import org.springframework.context.ApplicationEvent;

import javax.mail.Message;

public class EmailEvt extends ApplicationEvent {
    private String emailUUID;
    public EmailEvt(Message message) {
        super(message);
    }
    public String getEmailUUID() {
        return emailUUID;
    }
    public void setEmailUUID(String emailUUID) {
        this.emailUUID = emailUUID;
    }
}
