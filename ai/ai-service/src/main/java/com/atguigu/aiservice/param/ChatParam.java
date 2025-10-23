package com.atguigu.aiservice.param;

import lombok.Data;

@Data
public class ChatParam
{
    private String question;
    private String model;
    private String knowledge;
    private Long userId;
    private String conversationId;
    private Boolean toolCalling;
}
