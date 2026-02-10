package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.GiftDeviceInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 设备信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-10
 */
public interface GiftDeviceInfoMapper
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
     * 删除设备信息
     *
     * @param id 设备信息主键
     * @return 结果
     */
    public int deleteGiftDeviceInfoById(String id);

    /**
     * 批量删除设备信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGiftDeviceInfoByIds(String[] ids);

    /**
     * 查询是否存在deviceNumber
     *
     * @param deviceNumber 设备号码
     * @return 结果
     */
    public int countByDeviceNumber(@Param("deviceNumber") String deviceNumber);

    /**
     * 根据deviceNumber查询GiftDeviceInfo信息
     * @param deviceNumber
     * @return
     */
    public GiftDeviceInfo selectByDeviceNumber(@Param("deviceNumber") String deviceNumber);
}
