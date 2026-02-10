package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * @author boyo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GiftCardQueryVo extends BaseEntity {

    private String sender;
    private String subject;
    private String giftType;
    private String dtStr;
    private String code;
    private String orderNumber;
    private Long amount;
    private String extraNumber;
    private String usageType;
    private Long status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
