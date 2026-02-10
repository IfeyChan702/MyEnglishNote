package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.req.AppDeviceCreateReq;
import com.ruoyi.system.domain.resp.AppDeviceInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.GiftDeviceInfoMapper;
import com.ruoyi.system.domain.GiftDeviceInfo;
import com.ruoyi.system.service.IGiftDeviceInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-10
 */
@Service
public class GiftDeviceInfoServiceImpl implements IGiftDeviceInfoService
{
    @Autowired
    private GiftDeviceInfoMapper giftDeviceInfoMapper;

    /**
     * 查询设备信息
     *
     * @param id 设备信息主键
     * @return 设备信息
     */
    @Override
    public GiftDeviceInfo selectGiftDeviceInfoById(String id)
    {
        return giftDeviceInfoMapper.selectGiftDeviceInfoById(id);
    }

    /**
     * 查询设备信息列表
     *
     * @param giftDeviceInfo 设备信息
     * @return 设备信息
     */
    @Override
    public List<GiftDeviceInfo> selectGiftDeviceInfoList(GiftDeviceInfo giftDeviceInfo)
    {
        return giftDeviceInfoMapper.selectGiftDeviceInfoList(giftDeviceInfo);
    }

    /**
     * 新增设备信息
     *
     * @param giftDeviceInfo 设备信息
     * @return 结果
     */
    @Override
    public int insertGiftDeviceInfo(GiftDeviceInfo giftDeviceInfo)
    {
        giftDeviceInfo.setCreateTime(DateUtils.getNowDate());
        return giftDeviceInfoMapper.insertGiftDeviceInfo(giftDeviceInfo);
    }

    /**
     * 修改设备信息
     *
     * @param giftDeviceInfo 设备信息
     * @return 结果
     */
    @Override
    public int updateGiftDeviceInfo(GiftDeviceInfo giftDeviceInfo)
    {
        giftDeviceInfo.setUpdateTime(DateUtils.getNowDate());
        return giftDeviceInfoMapper.updateGiftDeviceInfo(giftDeviceInfo);
    }

    /**
     * 批量删除设备信息
     *
     * @param ids 需要删除的设备信息主键
     * @return 结果
     */
    @Override
    public int deleteGiftDeviceInfoByIds(String[] ids)
    {
        return giftDeviceInfoMapper.deleteGiftDeviceInfoByIds(ids);
    }

    /**
     * 删除设备信息信息
     *
     * @param id 设备信息主键
     * @return 结果
     */
    @Override
    public int deleteGiftDeviceInfoById(String id)
    {
        return giftDeviceInfoMapper.deleteGiftDeviceInfoById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFromApp(AppDeviceCreateReq req) {

        /*if (req.getRechargeLimit().compareTo(req.getCurrentAmount()) < 0) {
            throw new ServiceException("rechargeLimit 不能小于 currentAmount");
        }*/

        if (giftDeviceInfoMapper.countByDeviceNumber(req.getDeviceNumber()) > 0) {
            throw new ServiceException("设备号已存在");
        }

        GiftDeviceInfo entity = new GiftDeviceInfo();
        entity.setDeviceNumber(req.getDeviceNumber().trim());
        entity.setCurrentAmount(BigDecimal.valueOf(0));
        entity.setRechargeLimit(BigDecimal.valueOf(0));
        entity.setRechargeEnabled(0);
        entity.setShoppingEnabled(0);
        entity.setDeviceEnabled(0);
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setUpdateTime(DateUtils.getNowDate());

        try {
            giftDeviceInfoMapper.insertGiftDeviceInfo(entity);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("设备号已存在");
        }
        return entity.getId();
    }

    @Override
    public AppDeviceInfoResp getByDeviceNumber(String phone) {
        GiftDeviceInfo entity = giftDeviceInfoMapper.selectByDeviceNumber(phone);
        if (entity == null) {
            throw new ServiceException("未找到该手机号绑定的设备");
        }

        AppDeviceInfoResp resp = new AppDeviceInfoResp();
        resp.setPhone(entity.getDeviceNumber());
        resp.setAmount(entity.getCurrentAmount());
        resp.setLimit(entity.getRechargeLimit());
        resp.setRechargeEnabled(entity.getRechargeEnabled());
        resp.setShoppingEnabled(entity.getShoppingEnabled());
        resp.setDeviceEnabled(entity.getDeviceEnabled());
        return resp;
    }
}
