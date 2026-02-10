package com.ruoyi.system.domain.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author boyo
 */
@Data
public class AppDeviceInfoResp {

    private String phone;

    private BigDecimal amount;
    private BigDecimal limit;

    private Integer rechargeEnabled;
    private Integer shoppingEnabled;
    private Integer deviceEnabled;
}
