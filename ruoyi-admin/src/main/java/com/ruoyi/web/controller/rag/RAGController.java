package com.ruoyi.web.controller.rag;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.EnglishNote;
import com.ruoyi.system.domain.dto.RAGQueryRequest;
import com.ruoyi.system.domain.dto.RAGResponse;
import com.ruoyi.system.service.IRAGService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RAG功能Controller
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Api(tags = "RAG检索与问答")
@RestController
@RequestMapping("/api/rag")
public class RAGController extends BaseController {

    @Autowired
    private IRAGService ragService;
    
    /**
     * 向量检索相关笔记
     */
    @ApiOperation("向量检索相关笔记")
    @Log(title = "RAG检索", businessType = BusinessType.OTHER)
    @PostMapping("/search")
    public AjaxResult search(@Validated @RequestBody RAGQueryRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            List<EnglishNote> notes = ragService.searchNotes(
                    userId,
                    request.getQuestion(),
                    request.getSimilarityThreshold(),
                    request.getMaxResults()
            );
            
            return success(notes);
        } catch (Exception e) {
            logger.error("Failed to search notes: {}", e.getMessage(), e);
            return error("检索笔记失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取带上下文的回答
     */
    @ApiOperation("获取AI回答")
    @Log(title = "RAG问答", businessType = BusinessType.OTHER)
    @PostMapping("/answer")
    public AjaxResult answer(@Validated @RequestBody RAGQueryRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 设置默认值
            if (request.getIncludeContext() == null) {
                request.setIncludeContext(true);
            }
            
            RAGResponse response = ragService.generateAnswer(
                    userId,
                    request.getQuestion(),
                    request.getSimilarityThreshold(),
                    request.getMaxResults(),
                    request.getIncludeContext()
            );
            
            return success(response);
        } catch (Exception e) {
            logger.error("Failed to generate answer: {}", e.getMessage(), e);
            
            RAGResponse errorResponse = new RAGResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setProcessingTime(0L);
            
            return error(String.valueOf(errorResponse));
        }
    }
    
    /**
     * 多轮对话（简化版）
     * 此接口可以扩展为支持对话历史
     */
    @ApiOperation("多轮对话")
    @Log(title = "RAG对话", businessType = BusinessType.OTHER)
    @PostMapping("/chat")
    public AjaxResult chat(@Validated @RequestBody RAGQueryRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 目前使用相同的实现，后续可以扩展支持对话历史
            RAGResponse response = ragService.generateAnswer(
                    userId,
                    request.getQuestion(),
                    request.getSimilarityThreshold(),
                    request.getMaxResults(),
                    true // 多轮对话始终包含上下文
            );
            
            return success(response);
        } catch (Exception e) {
            logger.error("Failed to chat: {}", e.getMessage(), e);
            
            RAGResponse errorResponse = new RAGResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setProcessingTime(0L);
            
            return error(String.valueOf(errorResponse));
        }
    }
}
