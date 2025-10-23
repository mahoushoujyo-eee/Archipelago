package com.atguigu.blogrpc.service;

import com.atguigu.blogrpc.param.BlogParam;
import com.atguigu.blogrpc.param.Response;

public interface LikeRpc
{
    Response<Integer> getLikeCount(BlogParam blogParam);

    Response<String> addLike(BlogParam blogParam);

    Response<String> deleteLike(BlogParam blogParam);

    Response<Boolean> isLiked(BlogParam blogParam);
}
