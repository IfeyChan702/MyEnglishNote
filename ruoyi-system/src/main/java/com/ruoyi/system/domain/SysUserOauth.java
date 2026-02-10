package com.ruoyi.system.domain;

import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Getter;

@Getter
public class SysUserOauth {

    private Long id;

    private Long userId;

    /** google / apple / facebook */
    private String provider;

    /** Google 的 sub */
    private String providerUid;

    private String email;

    private String avatar;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderUid(String providerUid) {
        this.providerUid = providerUid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
