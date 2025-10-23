package com.atguigu.aiservice.entity;

import lombok.Data;

@Data
public class Conversation
{
    private Long id;
    private Long userId;
    private String conversationId;
    private String title;
}
