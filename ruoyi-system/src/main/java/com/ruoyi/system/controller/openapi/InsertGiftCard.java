package com.ruoyi.system.controller.openapi;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.GiftCard;
import com.ruoyi.system.mapper.GiftCardMapper;
import com.ruoyi.system.service.IGiftCardService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InsertGiftCard extends BaseController {
    @Autowired
    private IGiftCardService giftCardService;
    @Autowired
    GiftCardMapper giftCardMapper;

    //    "openapi"
    @ApiOperation("添加卡密接口")
    @Log(title = "礼品卡", businessType = BusinessType.INSERT)
    @GetMapping("/openapi/giftCard/add")
    public AjaxResult add(@RequestBody GiftCard giftCard) {
        // 检查是否重复
        GiftCard exist = giftCardMapper.selectGiftCardByCode(giftCard.getCode());
        if (exist != null) {
            throw new ServiceException("礼品卡代码已存在");
        }
        return toAjax(giftCardService.insertGiftCard(giftCard));
    }

    @ApiOperation("通过卡密更新卡密接口")
    @Log(title = "礼品卡", businessType = BusinessType.UPDATE)
    @PostMapping("/openapi/giftCard/updateByCode")
    public AjaxResult updateByCode(@RequestBody GiftCard giftCard)
    {
        // 检查是否重复
        if (giftCard == null || StringUtils.isEmpty(giftCard.getCode())) {
            return AjaxResult.error("卡密的code不能为空");
        }

        GiftCard exist = giftCardMapper.selectGiftCardByCode(giftCard.getCode());
        if (exist == null) {
            return AjaxResult.error("礼品卡代码不存在，请从新检查！");
        }

        if (exist.getStatus() != 3L){
            return AjaxResult.error("礼品卡的状态不是“正在使用中”");
        }
        return toAjax(giftCardService.updateGiftCardByCode(giftCard));
    }


    @ApiOperation("获取一个未使用的卡密的接口")
    @Log(title = "礼品卡", businessType = BusinessType.OTHER)
    @GetMapping("/openapi/giftCard/getGiftCardForOneNoUse")
    public AjaxResult list(@RequestHeader(value = "Authorization", required = false) String apiKey,
                           @RequestParam(value = "giftType") String giftType
    )
    {
        if (!"jsaklf29032ufjskfncfjajfi*722s32#2io23kjd".equals(apiKey)) {
            return AjaxResult.error("Unauthorized");
        }

        if (StringUtils.isEmpty(giftType)) {
            return AjaxResult.error("giftType 参数不能为空");
        }

        GiftCard card = giftCardService.getOldestGiftCardByStatus(0L, giftType.trim());

        if (card == null) {
            return AjaxResult.error("没有这个类型的礼品卡了");
        }

        return AjaxResult.success(card);
    }

    @GetMapping("/openapi/testApi")
    public String testApi() {
        return "test";
    }

}
