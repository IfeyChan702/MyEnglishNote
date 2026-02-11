package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Character;
import com.ruoyi.system.domain.dto.AddCharacterRequest;
import com.ruoyi.system.service.ICharacterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 故事主角管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
@Api(tags = "故事主角管理")
@RestController
@RequestMapping("/api/character")
public class CharacterController extends BaseController {

    @Autowired
    private ICharacterService characterService;
    
    /**
     * 添加主角
     */
    @ApiOperation("添加主角")
    @Log(title = "添加主角", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody AddCharacterRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            Character character = new Character();
            character.setUserId(userId);
            character.setName(request.getName());
            character.setDescription(request.getDescription());
            character.setAvatarUrl(request.getAvatarUrl());
            
            int result = characterService.insertCharacter(character);
            if (result > 0) {
                return success(character);
            }
            return error("添加主角失败");
        } catch (Exception e) {
            logger.error("Failed to add character: {}", e.getMessage(), e);
            return error("添加主角失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取主角列表
     */
    @ApiOperation("获取主角列表")
    @GetMapping("/list")
    public TableDataInfo list() {
        try {
            Long userId = SecurityUtils.getUserId();
            startPage();
            List<Character> list = characterService.selectCharacterListByUserId(userId);
            return getDataTable(list);
        } catch (Exception e) {
            logger.error("Failed to list characters: {}", e.getMessage(), e);
            return getDataTable(null);
        }
    }
    
    /**
     * 获取主角详情
     */
    @ApiOperation("获取主角详情")
    @GetMapping("/{id}")
    public AjaxResult getInfo(
            @ApiParam("主角ID") @PathVariable Long id) {
        try {
            Character character = characterService.selectCharacterById(id);
            if (character == null) {
                return error("主角不存在");
            }
            
            // 验证主角所有权
            Long userId = SecurityUtils.getUserId();
            if (!character.getUserId().equals(userId)) {
                return error("无权访问该主角");
            }
            
            return success(character);
        } catch (Exception e) {
            logger.error("Failed to get character: {}", e.getMessage(), e);
            return error("获取主角信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新主角
     */
    @ApiOperation("更新主角")
    @Log(title = "更新主角", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult update(
            @ApiParam("主角ID") @PathVariable Long id,
            @Validated @RequestBody AddCharacterRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证主角存在和所有权
            Character existingCharacter = characterService.selectCharacterById(id);
            if (existingCharacter == null) {
                return error("主角不存在");
            }
            if (!existingCharacter.getUserId().equals(userId)) {
                return error("无权修改该主角");
            }
            
            Character character = new Character();
            character.setId(id);
            character.setName(request.getName());
            character.setDescription(request.getDescription());
            character.setAvatarUrl(request.getAvatarUrl());
            
            int result = characterService.updateCharacter(character);
            if (result > 0) {
                return success("更新成功");
            }
            return error("更新失败");
        } catch (Exception e) {
            logger.error("Failed to update character: {}", e.getMessage(), e);
            return error("更新主角失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除主角
     */
    @ApiOperation("删除主角")
    @Log(title = "删除主角", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(
            @ApiParam("主角ID") @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证主角存在和所有权
            Character existingCharacter = characterService.selectCharacterById(id);
            if (existingCharacter == null) {
                return error("主角不存在");
            }
            if (!existingCharacter.getUserId().equals(userId)) {
                return error("无权删除该主角");
            }
            
            int result = characterService.deleteCharacterById(id);
            if (result > 0) {
                return success("删除成功");
            }
            return error("删除失败");
        } catch (Exception e) {
            logger.error("Failed to delete character: {}", e.getMessage(), e);
            return error("删除主角失败: " + e.getMessage());
        }
    }
}
