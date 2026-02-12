package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Story;
import com.ruoyi.system.domain.dto.GenerateStoryRequest;
import com.ruoyi.system.domain.dto.GenerateStoryResponse;
import com.ruoyi.system.service.IStoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 英语故事管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
@Api(tags = "英语故事管理")
@RestController
@RequestMapping("/api/story")
public class StoryController extends BaseController {

    @Autowired
    private IStoryService storyService;
    
    /**
     * 生成故事（核心功能）
     */
    @ApiOperation("生成故事")
    @Log(title = "生成故事", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@Validated @RequestBody GenerateStoryRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            GenerateStoryResponse response = storyService.generateStory(userId, request);
            
            if (response.isSuccess()) {
                return success(response);
            } else {
                return error(response.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to generate story: {}", e.getMessage(), e);
            return error("生成故事失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取故事列表
     */
    @ApiOperation("获取故事列表")
    @GetMapping("/list")
    public TableDataInfo list(
            @ApiParam("主角ID（可选）") @RequestParam(required = false) Long characterId,
            @ApiParam("是否收藏（可选）") @RequestParam(required = false) Boolean isFavorite) {
        try {
            Long userId = SecurityUtils.getUserId();
            startPage();
            
            Story query = new Story();
            query.setUserId(userId);
            if (characterId != null) {
                query.setCharacterId(characterId);
            }
            if (isFavorite != null) {
                query.setIsFavorite(isFavorite);
            }
            
            List<Story> list = storyService.selectStoryList(query);
            return getDataTable(list);
        } catch (Exception e) {
            logger.error("Failed to list stories: {}", e.getMessage(), e);
            return getDataTable(null);
        }
    }
    
    /**
     * 获取故事详情
     */
    @ApiOperation("获取故事详情")
    @GetMapping("/{id}")
    public AjaxResult getInfo(
            @ApiParam("故事ID") @PathVariable Long id) {
        try {
            Story story = storyService.selectStoryById(id);
            if (story == null) {
                return error("故事不存在");
            }
            
            // 验证故事所有权
            Long userId = SecurityUtils.getUserId();
            if (!story.getUserId().equals(userId)) {
                return error("无权访问该故事");
            }
            
            return success(story);
        } catch (Exception e) {
            logger.error("Failed to get story: {}", e.getMessage(), e);
            return error("获取故事信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新故事
     */
    @ApiOperation("更新故事")
    @Log(title = "更新故事", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult update(
            @ApiParam("故事ID") @PathVariable Long id,
            @RequestBody Map<String, Object> updateData) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证故事存在和所有权
            Story existingStory = storyService.selectStoryById(id);
            if (existingStory == null) {
                return error("故事不存在");
            }
            if (!existingStory.getUserId().equals(userId)) {
                return error("无权修改该故事");
            }
            
            Story story = new Story();
            story.setId(id);
            
            // 支持更新标题和内容
            if (updateData.containsKey("title")) {
                story.setTitle((String) updateData.get("title"));
            }
            if (updateData.containsKey("content")) {
                story.setContent((String) updateData.get("content"));
            }
            
            int result = storyService.updateStory(story);
            if (result > 0) {
                return success("更新成功");
            }
            return error("更新失败");
        } catch (Exception e) {
            logger.error("Failed to update story: {}", e.getMessage(), e);
            return error("更新故事失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除故事
     */
    @ApiOperation("删除故事")
    @Log(title = "删除故事", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(
            @ApiParam("故事ID") @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证故事存在和所有权
            Story existingStory = storyService.selectStoryById(id);
            if (existingStory == null) {
                return error("故事不存在");
            }
            if (!existingStory.getUserId().equals(userId)) {
                return error("无权删除该故事");
            }
            
            int result = storyService.deleteStoryById(id);
            if (result > 0) {
                return success("删除成功");
            }
            return error("删除失败");
        } catch (Exception e) {
            logger.error("Failed to delete story: {}", e.getMessage(), e);
            return error("删除故事失败: " + e.getMessage());
        }
    }
    
    /**
     * 收藏故事
     */
    @ApiOperation("收藏故事")
    @Log(title = "收藏故事", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/favorite")
    public AjaxResult favorite(
            @ApiParam("故事ID") @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证故事存在
            Story story = storyService.selectStoryById(id);
            if (story == null) {
                return error("故事不存在");
            }
            if (!story.getUserId().equals(userId)) {
                return error("无权收藏该故事");
            }
            
            int result = storyService.favoriteStory(userId, id);
            if (result > 0) {
                return success("收藏成功");
            }
            return success("已收藏");
        } catch (Exception e) {
            logger.error("Failed to favorite story: {}", e.getMessage(), e);
            return error("收藏故事失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消收藏故事
     */
    @ApiOperation("取消收藏故事")
    @Log(title = "取消收藏故事", businessType = BusinessType.UPDATE)
    @DeleteMapping("/{id}/favorite")
    public AjaxResult unfavorite(
            @ApiParam("故事ID") @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            int result = storyService.unfavoriteStory(userId, id);
            if (result > 0) {
                return success("取消收藏成功");
            }
            return success("未收藏");
        } catch (Exception e) {
            logger.error("Failed to unfavorite story: {}", e.getMessage(), e);
            return error("取消收藏失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成分享链接
     */
    @ApiOperation("生成分享链接")
    @Log(title = "分享故事", businessType = BusinessType.OTHER)
    @PostMapping("/{id}/share")
    public AjaxResult share(
            @ApiParam("故事ID") @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证故事存在和所有权
            Story story = storyService.selectStoryById(id);
            if (story == null) {
                return error("故事不存在");
            }
            if (!story.getUserId().equals(userId)) {
                return error("无权分享该故事");
            }
            
            String shareToken = storyService.generateShareToken(id);
            
            Map<String, String> result = new HashMap<>();
            result.put("shareToken", shareToken);
            result.put("shareUrl", "/api/story/shared/" + shareToken);
            
            return success(result);
        } catch (Exception e) {
            logger.error("Failed to generate share link: {}", e.getMessage(), e);
            return error("生成分享链接失败: " + e.getMessage());
        }
    }
    
    /**
     * 通过分享链接查看故事（无需登录）
     */
    @ApiOperation("通过分享链接查看故事")
    @GetMapping("/shared/{shareToken}")
    public AjaxResult getSharedStory(
            @ApiParam("分享Token") @PathVariable String shareToken) {
        try {
            Story story = storyService.getStoryByShareToken(shareToken);
            if (story == null) {
                return error("故事不存在或已被删除");
            }
            
            return success(story);
        } catch (Exception e) {
            logger.error("Failed to get shared story: {}", e.getMessage(), e);
            return error("获取故事失败: " + e.getMessage());
        }
    }
}
