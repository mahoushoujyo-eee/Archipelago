package com.atguigu.blogservice.service;

import com.atguigu.blogrpc.param.BlogParam;
import com.atguigu.blogrpc.param.Response;
import com.atguigu.blogrpc.service.LikeRpc;
import com.atguigu.blogservice.mapper.LikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService implements LikeRpc
{
    @Autowired
    private LikeMapper likeMapper;

    @Override
    public Response<Integer> getLikeCount(BlogParam blogParam)
    {
        return Response.success(likeMapper.getLikeCount(blogParam));
    }

    @Override
    public Response<String> addLike(BlogParam blogParam)
    {
        likeMapper.addLike(blogParam);
        return Response.success("添加成功");
    }

    @Override
    public Response<String> deleteLike(BlogParam blogParam)
    {
        likeMapper.deleteLike(blogParam);
        return Response.success("删除成功");
    }

    @Override
    public Response<Boolean> isLiked(BlogParam blogParam)
    {
        return Response.success(likeMapper.isLiked(blogParam));
    }
}
