package com.ruoyi.system.domain.dto.req;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @author boyo
 */
@Data
public class AppDeviceCreateReq {

    @NotBlank(message = "deviceNumber 不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,64}$", message = "deviceNumber 格式不合法(3-64位字母数字_-)")
    private String deviceNumber;

    //@NotNull(message = "currentAmount 不能为空")
    @DecimalMin(value = "0.00", message = "currentAmount 不能为负数")
    @Digits(integer = 12, fraction = 2, message = "currentAmount 最多12位整数2位小数")
    private BigDecimal currentAmount;

    //@NotNull(message = "rechargeLimit 不能为空")
    @DecimalMin(value = "0.00", message = "rechargeLimit 不能为负数")
    @Digits(integer = 12, fraction = 2, message = "rechargeLimit 最多12位整数2位小数")
    private BigDecimal rechargeLimit;

    @Min(value = 0, message = "rechargeEnabled 只能是0/1")
    @Max(value = 1, message = "rechargeEnabled 只能是0/1")
    //@NotNull(message = "rechargeEnabled 不能为空")
    private Integer rechargeEnabled;

    @Min(value = 0, message = "rechargeEnabled 只能是0/1")
    @Max(value = 1, message = "rechargeEnabled 只能是0/1")
    //@NotNull(message = "shoppingEnabled 不能为空")
    private Integer shoppingEnabled;

    @Min(value = 0, message = "rechargeEnabled 只能是0/1")
    @Max(value = 1, message = "rechargeEnabled 只能是0/1")
    //@NotNull(message = "deviceEnabled 不能为空")
    private Integer deviceEnabled;
}

