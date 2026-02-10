package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.SysUserOauth;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserOauthMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OauthLoginService {

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysUserOauthMapper oauthMapper;

    public SysUser oauthLogin(
            String provider,
            String providerUid,
            String email,
            String avatar,
            String userName
    ) {

        SysUserOauth oauth =
                oauthMapper.selectByProvider(provider, providerUid);

        if (oauth != null) {
            return userMapper.selectUserById(oauth.getUserId());
        }

        // 新用户
        SysUser user = new SysUser();
        // 如果 userName 为空或空字符串，自动生成
        if (userName == null || userName.trim().isEmpty()) {
            user.setUserName(provider + "_" + providerUid.substring(0, 8));
        } else {
            user.setUserName(userName);
        }
        user.setNickName(userName);
        user.setEmail(email);
        user.setStatus("0");

        userMapper.insertUser(user);

        SysUserOauth newOauth = new SysUserOauth();
        newOauth.setUserId(user.getUserId());
        newOauth.setProvider(provider);
        newOauth.setProviderUid(providerUid);
        newOauth.setEmail(email);
        newOauth.setAvatar(avatar);

        oauthMapper.insert(newOauth);

        return user;
    }
}
