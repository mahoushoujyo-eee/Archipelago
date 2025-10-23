package com.atguigu.aiservice.entity;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class Message
{
    private Long id;
    private String conversationId;
    private String content;
    private String type;
    private Timestamp timestamp;
}
