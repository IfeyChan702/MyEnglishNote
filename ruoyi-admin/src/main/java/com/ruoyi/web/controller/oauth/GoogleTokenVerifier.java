package com.ruoyi.web.controller.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.ruoyi.web.controller.oauth.dto.GoogleTokenInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class GoogleTokenVerifier {

    private static final String CLIENT_ID = "746450421934-60f53v3qo7d950rddmndsa3bmdga0rpf.apps.googleusercontent.com";

    public static GoogleTokenInfo verifyAccessToken(String accessToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<GoogleTokenInfo> resp =
                restTemplate.getForEntity(url, GoogleTokenInfo.class);

        GoogleTokenInfo info = resp.getBody();

        // 1️⃣ audience 校验（非常重要）
        if (!CLIENT_ID.equals(info.getAud())) {
            throw new RuntimeException("Invalid Google client_id");
        }

        // 2️⃣ 是否过期
        if (info.getExpires_in() <= 0) {
            throw new RuntimeException("Google token expired");
        }

        return info;
    }
}
