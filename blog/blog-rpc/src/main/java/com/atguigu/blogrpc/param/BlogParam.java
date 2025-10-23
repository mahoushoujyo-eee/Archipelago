package com.atguigu.blogrpc.param;

import lombok.Data;

import java.util.Date;

@Data
public class BlogParam
{
    private Long id;

    private String title;

    private Long userId;

    private String contentUrl;

    private String nickname;

    private String ossId;

    private String bucketName;

    private String imgUrl;

    private String imgName;

    private String tags;

    private Integer pageIndex;

    private Integer pageOffset;

    private Date creationTime;

    private Integer type;

    private Integer likes;

    private Integer comments;
}
