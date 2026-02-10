package com.ruoyi.web.controller.rag;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.EnglishNote;
import com.ruoyi.system.service.INoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 笔记管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Api(tags = "笔记管理")
@RestController
@RequestMapping("/api/note")
public class NoteController extends BaseController {

    @Autowired
    private INoteService noteService;
    
    /**
     * 添加笔记
     */
    @ApiOperation("添加笔记")
    @Log(title = "添加笔记", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody EnglishNote note) {
        try {
            // 设置当前用户ID
            Long userId = SecurityUtils.getUserId();
            note.setUserId(userId);
            
            EnglishNote createdNote = noteService.createNote(note);
            return success(createdNote);
        } catch (Exception e) {
            logger.error("Failed to add note: {}", e.getMessage(), e);
            return error("添加笔记失败: " + e.getMessage());
        }
    }
    
    /**
     * 列出笔记
     */
    @ApiOperation("列出笔记")
    @GetMapping("/list")
    public TableDataInfo list() {
        try {
            Long userId = SecurityUtils.getUserId();
            startPage();
            List<EnglishNote> list = noteService.listNotes(userId);
            return getDataTable(list);
        } catch (Exception e) {
            logger.error("Failed to list notes: {}", e.getMessage(), e);
            return getDataTable(null);
        }
    }
    
    /**
     * 获取笔记详情
     */
    @ApiOperation("获取笔记详情")
    @GetMapping("/{id}")
    public AjaxResult getInfo(
            @ApiParam("笔记ID") @PathVariable Long id) {
        try {
            EnglishNote note = noteService.getNoteById(id);
            if (note == null) {
                return error("笔记不存在");
            }
            
            // 验证笔记所有权
            Long userId = SecurityUtils.getUserId();
            if (!note.getUserId().equals(userId)) {
                return error("无权访问此笔记");
            }
            
            return success(note);
        } catch (Exception e) {
            logger.error("Failed to get note: {}", e.getMessage(), e);
            return error("获取笔记失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新笔记
     */
    @ApiOperation("更新笔记")
    @Log(title = "更新笔记", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult update(
            @ApiParam("笔记ID") @PathVariable Long id,
            @Validated @RequestBody EnglishNote note) {
        try {
            // 验证笔记所有权
            EnglishNote existingNote = noteService.getNoteById(id);
            if (existingNote == null) {
                return error("笔记不存在");
            }
            
            Long userId = SecurityUtils.getUserId();
            if (!existingNote.getUserId().equals(userId)) {
                return error("无权修改此笔记");
            }
            
            note.setId(id);
            note.setUserId(userId);
            
            EnglishNote updatedNote = noteService.updateNote(note);
            return success(updatedNote);
        } catch (Exception e) {
            logger.error("Failed to update note: {}", e.getMessage(), e);
            return error("更新笔记失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除笔记
     */
    @ApiOperation("删除笔记")
    @Log(title = "删除笔记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(
            @ApiParam("笔记ID") @PathVariable Long id) {
        try {
            // 验证笔记所有权
            EnglishNote existingNote = noteService.getNoteById(id);
            if (existingNote == null) {
                return error("笔记不存在");
            }
            
            Long userId = SecurityUtils.getUserId();
            if (!existingNote.getUserId().equals(userId)) {
                return error("无权删除此笔记");
            }
            
            boolean success = noteService.deleteNote(id);
            return toAjax(success);
        } catch (Exception e) {
            logger.error("Failed to delete note: {}", e.getMessage(), e);
            return error("删除笔记失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除笔记
     */
    @ApiOperation("批量删除笔记")
    @Log(title = "批量删除笔记", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult batchDelete(
            @ApiParam("笔记ID数组") @RequestBody Long[] ids) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证所有笔记的所有权
            for (Long id : ids) {
                EnglishNote note = noteService.getNoteById(id);
                if (note == null || !note.getUserId().equals(userId)) {
                    return error("无权删除某些笔记");
                }
            }
            
            boolean success = noteService.deleteNotes(ids);
            return toAjax(success);
        } catch (Exception e) {
            logger.error("Failed to batch delete notes: {}", e.getMessage(), e);
            return error("批量删除笔记失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计笔记数量
     */
    @ApiOperation("统计笔记数量")
    @GetMapping("/count")
    public AjaxResult count() {
        try {
            Long userId = SecurityUtils.getUserId();
            int count = noteService.countNotes(userId);
            return success(count);
        } catch (Exception e) {
            logger.error("Failed to count notes: {}", e.getMessage(), e);
            return error("统计笔记失败: " + e.getMessage());
        }
    }
}
