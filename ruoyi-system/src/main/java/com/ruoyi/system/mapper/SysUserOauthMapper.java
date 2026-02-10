package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysUserOauth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserOauthMapper {

    SysUserOauth selectByProvider(
            @Param("provider") String provider,
            @Param("providerUid") String providerUid
    );

    int insert(SysUserOauth oauth);
}
