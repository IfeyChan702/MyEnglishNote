package com.ruoyi.web.controller.rag.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateNoteRequest {

    @NotBlank(message = "笔记内容不能为空")
    private String content;

    private String tags;

    private String title;

    private String type;
}