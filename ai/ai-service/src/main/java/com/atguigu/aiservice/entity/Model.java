package com.atguigu.aiservice.entity;

import lombok.Data;

@Data
public class Model
{
    private Long id;
    private String modelName;
    private String description;
    private Integer level;
}
