package com.ruoyi.system.controller.app;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.GiftDeviceInfo;
import com.ruoyi.system.domain.dto.req.AppDeviceCreateReq;
import com.ruoyi.system.domain.resp.AppDeviceInfoResp;
import com.ruoyi.system.service.IGiftDeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @author boyo
 */
@RestController
@RequestMapping("/api/app/device")
@Validated
public class AppGiftDeviceController {

    @Autowired
    private IGiftDeviceInfoService giftDeviceInfoService;

    /**
     * App 端：创建设备
     * 说明：
     */
    @PostMapping
    public AjaxResult create(@Valid @RequestBody AppDeviceCreateReq req) {
        Long id = giftDeviceInfoService.createFromApp(req);
        return AjaxResult.success().put("id", id);
    }

    @GetMapping("/phone")
    public AjaxResult listByNumber(
            @RequestParam("phone")
            @NotBlank(message = "phone 不能为空")
            @Pattern(regexp = "^[0-9+]{6,20}$", message = "phone 格式不合法(6-20位数字或+号)")
            String phone
    ){
        AppDeviceInfoResp resp = giftDeviceInfoService.getByDeviceNumber(phone.trim());
        return AjaxResult.success(resp);
    }
}
