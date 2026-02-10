package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * RAG响应DTO
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@ApiModel("RAG响应结果")
public class RAGResponse
{
    @ApiModelProperty("LLM生成的回答")
    private String answer;

    @ApiModelProperty("相关笔记列表")
    private List<NoteDTO> relatedNotes;

    @ApiModelProperty("检索到的笔记数量")
    private Integer noteCount;

    @ApiModelProperty("处理时间(毫秒)")
    private Long processingTime;

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<NoteDTO> getRelatedNotes() {
        return relatedNotes;
    }

    public void setRelatedNotes(List<NoteDTO> relatedNotes) {
        this.relatedNotes = relatedNotes;
    }

    public Integer getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(Integer noteCount) {
        this.noteCount = noteCount;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
