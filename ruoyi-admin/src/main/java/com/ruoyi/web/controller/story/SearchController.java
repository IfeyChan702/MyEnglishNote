package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Character;
import com.ruoyi.system.domain.Story;
import com.ruoyi.system.service.ICharacterService;
import com.ruoyi.system.service.IStoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一搜索Controller
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
@Api(tags = "统一搜索")
@RestController
@RequestMapping("/api/search")
public class SearchController extends BaseController {

    private static final Double DEFAULT_SIMILARITY_THRESHOLD = 0.7;
    private static final Integer DEFAULT_MAX_RESULTS = 10;

    @Autowired
    private IStoryService storyService;

    @Autowired
    private ICharacterService characterService;
    
    /**
     * 搜索故事
     */
    @ApiOperation("搜索故事")
    @Log(title = "搜索故事", businessType = BusinessType.OTHER)
    @PostMapping("/stories")
    public AjaxResult searchStories(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("相似度阈值") @RequestParam(required = false) Double threshold,
            @ApiParam("最大结果数") @RequestParam(required = false) Integer maxResults) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // Apply defaults if not provided
            Double actualThreshold = threshold != null ? threshold : DEFAULT_SIMILARITY_THRESHOLD;
            Integer actualMaxResults = maxResults != null ? maxResults : DEFAULT_MAX_RESULTS;
            
            List<Story> stories = storyService.searchStories(userId, keyword, actualThreshold, actualMaxResults);
            
            return success(stories);
        } catch (Exception e) {
            logger.error("Failed to search stories: {}", e.getMessage(), e);
            return error("搜索故事失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索主角
     */
    @ApiOperation("搜索主角")
    @Log(title = "搜索主角", businessType = BusinessType.OTHER)
    @PostMapping("/characters")
    public AjaxResult searchCharacters(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            List<Character> characters = characterService.searchCharacters(userId, keyword);
            
            return success(characters);
        } catch (Exception e) {
            logger.error("Failed to search characters: {}", e.getMessage(), e);
            return error("搜索主角失败: " + e.getMessage());
        }
    }
    
    /**
     * 全局搜索（包括故事和主角）
     */
    @ApiOperation("全局搜索")
    @Log(title = "全局搜索", businessType = BusinessType.OTHER)
    @PostMapping("/all")
    public AjaxResult searchAll(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("相似度阈值") @RequestParam(required = false) Double threshold,
            @ApiParam("最大结果数") @RequestParam(required = false) Integer maxResults) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // Apply defaults if not provided
            Double actualThreshold = threshold != null ? threshold : DEFAULT_SIMILARITY_THRESHOLD;
            Integer actualMaxResults = maxResults != null ? maxResults : DEFAULT_MAX_RESULTS;
            
            // 搜索故事
            List<Story> stories = storyService.searchStories(userId, keyword, actualThreshold, actualMaxResults);
            
            // 搜索主角
            List<Character> characters = characterService.searchCharacters(userId, keyword);
            
            // 组合结果
            Map<String, Object> result = new HashMap<>();
            result.put("stories", stories);
            result.put("characters", characters);
            result.put("totalStories", stories.size());
            result.put("totalCharacters", characters.size());
            
            return success(result);
        } catch (Exception e) {
            logger.error("Failed to search all: {}", e.getMessage(), e);
            return error("全局搜索失败: " + e.getMessage());
        }
    }
}
