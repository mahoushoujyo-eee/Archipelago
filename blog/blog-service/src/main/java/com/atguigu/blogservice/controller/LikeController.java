package com.atguigu.blogservice.controller;

import com.atguigu.blogrpc.param.BlogParam;
import com.atguigu.blogrpc.param.Response;
import com.atguigu.blogservice.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikeController
{
    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    public Response<String> addLike(@RequestBody BlogParam blogParam)
    {
        likeService.addLike(blogParam);

        return Response.success("添加成功");
    }

    @PostMapping("/delete")
    public Response<String> deleteLike(@RequestBody BlogParam blogParam)
    {
        likeService.deleteLike(blogParam);

        return Response.success("删除成功");
    }

    @PostMapping("/get-count")
    public Response<Integer> getLikeCount(@RequestBody BlogParam blogParam)
    {
        return likeService.getLikeCount(blogParam);
    }

    @PostMapping("/is-liked")
    public Response<Boolean> isLiked(@RequestBody BlogParam blogParam)
    {
        return likeService.isLiked(blogParam);
    }

}
