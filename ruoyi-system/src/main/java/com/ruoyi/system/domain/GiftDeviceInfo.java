package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备信息对象 gift_device_info
 *
 * @author ruoyi
 * @date 2025-12-10
 */
public class GiftDeviceInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备号码 */
    @Excel(name = "设备号码")
    private String deviceNumber;

    /** 当前设备金额 */
    @Excel(name = "当前设备金额")
    private BigDecimal currentAmount;

    /** 充值金额上限 */
    @Excel(name = "充值金额上限")
    private BigDecimal rechargeLimit;

    /** 充值开关：1-开启，0-关闭 */
    @Excel(name = "充值开关：1-开启，0-关闭")
    private Integer rechargeEnabled;

    /** 购物开关：1-开启，0-关闭 */
    @Excel(name = "购物开关：1-开启，0-关闭")
    private Integer shoppingEnabled;

    /** 设备开关：1-启用，0-停用 */
    @Excel(name = "设备开关：1-启用，0-停用")
    private Integer deviceEnabled;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setDeviceNumber(String deviceNumber)
    {
        this.deviceNumber = deviceNumber;
    }

    public String getDeviceNumber()
    {
        return deviceNumber;
    }

    public void setCurrentAmount(BigDecimal currentAmount)
    {
        this.currentAmount = currentAmount;
    }

    public BigDecimal getCurrentAmount()
    {
        return currentAmount;
    }

    public void setRechargeLimit(BigDecimal rechargeLimit)
    {
        this.rechargeLimit = rechargeLimit;
    }

    public BigDecimal getRechargeLimit()
    {
        return rechargeLimit;
    }

    public void setRechargeEnabled(Integer rechargeEnabled)
    {
        this.rechargeEnabled = rechargeEnabled;
    }

    public Integer getRechargeEnabled()
    {
        return rechargeEnabled;
    }

    public void setShoppingEnabled(Integer shoppingEnabled)
    {
        this.shoppingEnabled = shoppingEnabled;
    }

    public Integer getShoppingEnabled()
    {
        return shoppingEnabled;
    }

    public void setDeviceEnabled(Integer deviceEnabled)
    {
        this.deviceEnabled = deviceEnabled;
    }

    public Integer getDeviceEnabled()
    {
        return deviceEnabled;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deviceNumber", getDeviceNumber())
                .append("currentAmount", getCurrentAmount())
                .append("rechargeLimit", getRechargeLimit())
                .append("rechargeEnabled", getRechargeEnabled())
                .append("shoppingEnabled", getShoppingEnabled())
                .append("deviceEnabled", getDeviceEnabled())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
