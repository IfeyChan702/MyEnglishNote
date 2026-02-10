package com.ruoyi.web.controller.rag;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.ReviewRecord;
import com.ruoyi.system.service.IReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 复习管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Api(tags = "复习管理")
@RestController
@RequestMapping("/api/review")
public class ReviewController extends BaseController {

    @Autowired
    private IReviewService reviewService;
    
    /**
     * 获取下一个待复习项目
     */
    @ApiOperation("获取待复习项目")
    @GetMapping("/next")
    public AjaxResult getNext(
            @ApiParam("返回数量") @RequestParam(defaultValue = "1") Integer limit) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            List<ReviewRecord> reviews = reviewService.getNextReviewItems(userId, limit);
            
            if (reviews == null || reviews.isEmpty()) {
                return success("暂无待复习项目", null);
            }
            
            // 如果只请求一个，返回单个对象；否则返回列表
            if (limit == 1) {
                return success(reviews.get(0));
            } else {
                return success(reviews);
            }
        } catch (Exception e) {
            logger.error("Failed to get next review items: {}", e.getMessage(), e);
            return error("获取复习项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录复习结果
     */
    @ApiOperation("记录复习结果")
    @Log(title = "记录复习", businessType = BusinessType.INSERT)
    @PostMapping("/record")
    public AjaxResult recordReview(
            @ApiParam("笔记ID") @RequestParam Long noteId,
            @ApiParam("复习质量(0-5)") @RequestParam Integer quality) {
        try {
            // 验证质量参数
            if (quality < 0 || quality > 5) {
                return error("复习质量必须在0-5之间");
            }
            
            Long userId = SecurityUtils.getUserId();
            
            ReviewRecord record = reviewService.recordReview(noteId, userId, quality);
            return success(record);
        } catch (Exception e) {
            logger.error("Failed to record review: {}", e.getMessage(), e);
            return error("记录复习失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取复习统计
     */
    @ApiOperation("获取复习统计")
    @GetMapping("/stats")
    public AjaxResult getStats() {
        try {
            Long userId = SecurityUtils.getUserId();
            
            Map<String, Object> stats = reviewService.getReviewStats(userId);
            return success(stats);
        } catch (Exception e) {
            logger.error("Failed to get review stats: {}", e.getMessage(), e);
            return error("获取复习统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化笔记的复习记录
     */
    @ApiOperation("初始化复习记录")
    @Log(title = "初始化复习", businessType = BusinessType.INSERT)
    @PostMapping("/initialize")
    public AjaxResult initialize(
            @ApiParam("笔记ID") @RequestParam Long noteId) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            ReviewRecord record = reviewService.initializeReview(noteId, userId);
            return success(record);
        } catch (Exception e) {
            logger.error("Failed to initialize review: {}", e.getMessage(), e);
            return error("初始化复习失败: " + e.getMessage());
        }
    }
}
