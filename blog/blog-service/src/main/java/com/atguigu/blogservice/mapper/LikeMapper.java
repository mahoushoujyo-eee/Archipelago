package com.atguigu.blogservice.mapper;

import com.atguigu.blogrpc.param.BlogParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper
{
    Integer getLikeCount(BlogParam blogParam);

    void addLike(BlogParam blogParam);

    void deleteLike(BlogParam blogParam);

    Boolean isLiked(BlogParam blogParam);
}
