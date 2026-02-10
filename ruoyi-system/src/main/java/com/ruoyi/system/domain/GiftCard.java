package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 礼品卡对象 gift_card
 *
 * @author ifey
 * @date 2025-12-02
 */
public class GiftCard extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 发件人 */
    @Excel(name = "发件人")
    private String sender;

    /** 主题 */
    @Excel(name = "主题")
    private String subject;

    /** 类型 亚马逊卡 0 苹果卡 1 google卡 2*/
    @Excel(name = "类型")
    private String giftType;

    /** 时间 */
    @Excel(name = "时间")
    private String dtStr;

    /** 礼品卡代码 */
    @Excel(name = "礼品卡代码")
    private String code;

    /** 订单号 ，亚马逊订单号*/
    @Excel(name = "订单号")
    private String orderNumber;

    /** 金额 */
    @Excel(name = "金额")
    private Long amount;

    /** 编号 对应军哥平台的单号，他们用来回调用的，标注了每个订单*/
    private String extraNumber;

    /** 使用类型 未使用 -1 承兑 2 充值话费 1 充值到亚马逊账号 0*/
    @Excel(name = "使用类型")
    private String usageType;

    /** 状态(0创建,1已使用)*/
    @Excel(name = "状态(0创建,1已使用,2错误,3正在使用中)")
    private Long status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setGiftType(String giftType)
    {
        this.giftType = giftType;
    }

    public String getGiftType()
    {
        return giftType;
    }

    public void setDtStr(String dtStr)
    {
        this.dtStr = dtStr;
    }

    public String getDtStr()
    {
        return dtStr;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setOrderNumber(String orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber()
    {
        return orderNumber;
    }

    public void setAmount(Long amount)
    {
        this.amount = amount;
    }

    public Long getAmount()
    {
        return amount;
    }

    public void setExtraNumber(String extraNumber)
    {
        this.extraNumber = extraNumber;
    }

    public String getExtraNumber()
    {
        return extraNumber;
    }

    public void setUsageType(String usageType)
    {
        this.usageType = usageType;
    }

    public String getUsageType()
    {
        return usageType;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sender", getSender())
            .append("subject", getSubject())
            .append("giftType", getGiftType())
            .append("dtStr", getDtStr())
            .append("code", getCode())
            .append("orderNumber", getOrderNumber())
            .append("amount", getAmount())
            .append("extraNumber", getExtraNumber())
            .append("usageType", getUsageType())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
