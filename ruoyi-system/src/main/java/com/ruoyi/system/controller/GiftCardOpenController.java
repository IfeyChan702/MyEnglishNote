package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.GiftCard;
import com.ruoyi.system.service.IGiftCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 礼品卡Controller
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@RestController
@RequestMapping("/openapi/GiftCard")
public class GiftCardOpenController extends BaseController
{
    @Autowired
    private IGiftCardService giftCardService;

    /**
     * 开放的新增礼品卡接口（无权限限制）
     */
    @Log(title = "礼品卡开放新增", businessType = BusinessType.INSERT)
    @PostMapping("/openInsert")
    public AjaxResult openInsert(@RequestBody GiftCard giftCard)
    {
        return toAjax(giftCardService.insertGiftCard(giftCard));
    }

}
