package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.GiftDeviceInfo;
import com.ruoyi.system.domain.dto.req.AppDeviceCreateReq;
import com.ruoyi.system.domain.resp.AppDeviceInfoResp;

/**
 * 设备信息Service接口
 *
 * @author ruoyi
 * @date 2025-12-10
 */
public interface IGiftDeviceInfoService
{
    /**
     * 查询设备信息
     *
     * @param id 设备信息主键
     * @return 设备信息
     */
    public GiftDeviceInfo selectGiftDeviceInfoById(String id);

    /**
     * 查询设备信息列表
     *
     * @param giftDeviceInfo 设备信息
     * @return 设备信息集合
     */
    public List<GiftDeviceInfo> selectGiftDeviceInfoList(GiftDeviceInfo giftDeviceInfo);

    /**
     * 新增设备信息
     *
     * @param giftDeviceInfo 设备信息
     * @return 结果
     */
    public int insertGiftDeviceInfo(GiftDeviceInfo giftDeviceInfo);

    /**
     * 修改设备信息
     *
     * @param giftDeviceInfo 设备信息
     * @return 结果
     */
    public int updateGiftDeviceInfo(GiftDeviceInfo giftDeviceInfo);

    /**
     * 批量删除设备信息
     *
     * @param ids 需要删除的设备信息主键集合
     * @return 结果
     */
    public int deleteGiftDeviceInfoByIds(String[] ids);

    /**
     * 删除设备信息信息
     *
     * @param id 设备信息主键
     * @return 结果
     */
    public int deleteGiftDeviceInfoById(String id);


    /**
     * 创建设备信息
     * @param req
     * @return AppDeviceCreateReq
     */
    public Long createFromApp(AppDeviceCreateReq req);

    /**
     * 根据手机号查询设备信息
     * @param  phone
     * @return AppDeviceInfoResp
     */
    public AppDeviceInfoResp getByDeviceNumber(String phone);
}
