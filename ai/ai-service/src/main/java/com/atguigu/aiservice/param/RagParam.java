package com.atguigu.aiservice.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RagParam
{
    private Long id;
    private String title;
    private String collectionName;
    private String description;
    private Long userId;
    private Date creationTime;

    private List<FileParam> files;
}
