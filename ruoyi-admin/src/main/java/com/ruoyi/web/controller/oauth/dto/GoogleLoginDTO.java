package com.ruoyi.web.controller.oauth.dto;

import lombok.Data;

@Data
public class GoogleLoginDTO {


    /**
     * Google OAuth2 Access Token
     */
    private String accessToken;

    /**
     * 用户头像（可选，前端传）
     */
    private String avatar;

    /**
     * Google 用户唯一 ID
     */
    private String sub;

    /**
     * google 用户名
     */
    private String userName;
}
