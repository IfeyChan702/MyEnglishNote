package com.ruoyi.web.controller.oauth;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.service.impl.OauthLoginService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.controller.oauth.dto.GoogleLoginDTO;
import com.ruoyi.web.controller.oauth.dto.GoogleTokenInfo;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import javax.annotation.Resource;

import java.util.Map;

@RestController
@RequestMapping("/api/authlogin")
public class GoogleAuthController {

    @Resource
    private OauthLoginService oauthLoginService;

    @Resource
    private TokenService tokenService;

    @PostMapping("/google")
    public AjaxResult googleLogin(@RequestBody GoogleLoginDTO dto) {

        // ① 校验 Google AccessToken
        GoogleTokenInfo tokenInfo = GoogleTokenVerifier.verifyAccessToken(dto.getAccessToken());

        String googleUid = tokenInfo.getSub();
        String email = tokenInfo.getEmail();
        String userName = dto.getUserName();

        // ② 业务账号系统，新用户自动注册，旧用户返回用户信息，然后登录
        SysUser user = oauthLoginService.oauthLogin(
                "google",
                googleUid,
                email,
                dto.getAvatar(),
                userName
        );

        // ③ 构建 LoginUser
        LoginUser loginUser = new LoginUser(user, Collections.emptySet());

        // ④ 签发你自己的 JWT（一年有效）
        String token = tokenService.createToken(loginUser);

        return AjaxResult.success(Map.of(
                "token", token,
                "user", user
        ));
    }



}
