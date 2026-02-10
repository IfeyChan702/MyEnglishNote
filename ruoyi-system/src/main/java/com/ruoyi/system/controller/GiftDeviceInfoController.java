package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.system.domain.GiftDeviceInfo;
import com.ruoyi.system.service.IGiftDeviceInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备信息Controller
 *
 * @author ruoyi
 * @date 2025-12-10
 */
@RestController
@RequestMapping("/system/info")
public class GiftDeviceInfoController extends BaseController
{
    @Autowired
    private IGiftDeviceInfoService giftDeviceInfoService;

    /**
     * 查询设备信息列表
     */
    @PreAuthorize("@ss.hasPermi('device:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiftDeviceInfo giftDeviceInfo)
    {
        startPage();
        List<GiftDeviceInfo> list = giftDeviceInfoService.selectGiftDeviceInfoList(giftDeviceInfo);
        return getDataTable(list);
    }

    /**
     * 导出设备信息列表
     */
    @PreAuthorize("@ss.hasPermi('device:info:export')")
    @Log(title = "设备信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GiftDeviceInfo giftDeviceInfo)
    {
        List<GiftDeviceInfo> list = giftDeviceInfoService.selectGiftDeviceInfoList(giftDeviceInfo);
        ExcelUtil<GiftDeviceInfo> util = new ExcelUtil<GiftDeviceInfo>(GiftDeviceInfo.class);
        util.exportExcel(response, list, "设备信息数据");
    }

    /**
     * 获取设备信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(giftDeviceInfoService.selectGiftDeviceInfoById(id));
    }

    /**
     * 新增设备信息
     */
    @PreAuthorize("@ss.hasPermi('device:info:add')")
    @Log(title = "设备信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiftDeviceInfo giftDeviceInfo)
    {
        return toAjax(giftDeviceInfoService.insertGiftDeviceInfo(giftDeviceInfo));
    }

    /**
     * 修改设备信息
     */
    @PreAuthorize("@ss.hasPermi('device:info:edit')")
    @Log(title = "设备信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiftDeviceInfo giftDeviceInfo)
    {
        return toAjax(giftDeviceInfoService.updateGiftDeviceInfo(giftDeviceInfo));
    }

    /**
     * 删除设备信息
     */
    @PreAuthorize("@ss.hasPermi('device:info:remove')")
    @Log(title = "设备信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(giftDeviceInfoService.deleteGiftDeviceInfoByIds(ids));
    }
}
