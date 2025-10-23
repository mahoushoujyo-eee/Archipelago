package com.atguigu.blogrpc.param;

import lombok.Data;

import java.util.Date;


@Data
public class CommentParam
{
    private Long id;

    private Long blogId;

    private Long userId;

    private String nickname;

    private Integer type;

    private String content;

    private Integer pageIndex;

    private Integer pageOffset;

    private Date creationTime;
}
