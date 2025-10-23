package com.atguigu.aiservice.param;

import lombok.Data;

@Data
public class FileParam
{
    private Long userId;
    private String url;
    private String name;
    private String contentType;
}
