package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.vo.GiftCardQueryVo;
import com.ruoyi.system.mapper.GiftCardMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.GiftCard;
import com.ruoyi.system.service.IGiftCardService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 礼品卡Controller
 *
 * @author ifey
 * @date 2025-12-02
 */
@RestController
@RequestMapping("/GiftCard/GiftCard")
public class GiftCardController extends BaseController
{
    @Autowired
    private IGiftCardService giftCardService;
    @Autowired
    GiftCardMapper giftCardMapper;

    /**
     * 查询礼品卡列表
     */
    @PreAuthorize("@ss.hasPermi('GiftCard:GiftCard:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiftCardQueryVo cardQueryVo)
    {
        startPage();
        List<GiftCard> list = giftCardService.selectGiftCardList(cardQueryVo);
        Double totalAmount = giftCardService.selectGiftCardAmountSum(cardQueryVo);

        TableDataInfo rspData = getDataTable(list);
        rspData.setMsg("总金额: " + (totalAmount == null ? 0 : totalAmount));
        return rspData;
    }

    /**
     * 导出礼品卡列表
     */
    @PreAuthorize("@ss.hasPermi('GiftCard:GiftCard:export')")
    @Log(title = "礼品卡", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GiftCard giftCard)
    {
        List<GiftCard> list = giftCardService.selectGiftCardList(giftCard);
        ExcelUtil<GiftCard> util = new ExcelUtil<GiftCard>(GiftCard.class);
        util.exportExcel(response, list, "礼品卡数据");
    }

    /**
     * 获取礼品卡详细信息
     */
    @PreAuthorize("@ss.hasPermi('GiftCard:GiftCard:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(giftCardService.selectGiftCardById(id));
    }

    /**
     * 新增礼品卡
     */
    @PreAuthorize("@ss.hasPermi('GiftCard:GiftCard:add')")
    @Log(title = "礼品卡", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiftCard giftCard)
    {
        return addOnSystem(giftCard);
    }
    /**
     * 新增礼品卡内部调用
     */
    public AjaxResult addOnSystem(@RequestBody GiftCard giftCard)
    {
        // 检查是否重复
        GiftCard exist = giftCardMapper.selectGiftCardByCode(giftCard.getCode());
        if (exist != null) {
            throw new ServiceException("礼品卡代码已存在");
        }
        return toAjax(giftCardService.insertGiftCard(giftCard));
    }
    /**
     * 修改礼品卡
     */
    @PreAuthorize("@ss.hasPermi('GiftCard:GiftCard:edit')")
    @Log(title = "礼品卡", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiftCard giftCard)
    {
        return toAjax(giftCardService.updateGiftCard(giftCard));
    }

    /**
     * 删除礼品卡
     */
    @PreAuthorize("@ss.hasPermi('GiftCard:GiftCard:remove')")
    @Log(title = "礼品卡", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(giftCardService.deleteGiftCardByIds(ids));
    }

}
