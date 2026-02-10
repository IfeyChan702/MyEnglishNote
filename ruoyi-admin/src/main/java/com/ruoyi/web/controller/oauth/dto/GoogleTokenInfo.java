package com.ruoyi.web.controller.oauth.dto;

import lombok.Data;

@Data
public class GoogleTokenInfo {

    /**
     * Google OAuth Client ID（audience）
     */
    private String aud;

    /**
     * Google 用户唯一 ID
     */
    private String sub;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 邮箱是否已验证（"true"/"false"）
     */
    private String email_verified;

    /**
     * token 剩余有效时间（秒）
     */
    private int expires_in;

    /**
     * 授权 scope
     */
    private String scope;
}
