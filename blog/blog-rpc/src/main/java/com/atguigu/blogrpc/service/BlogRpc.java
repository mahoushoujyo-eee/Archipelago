package com.atguigu.blogrpc.service;

import com.atguigu.blogrpc.param.BlogParam;
import com.atguigu.blogrpc.param.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface BlogRpc
{
    Response<List<URL>> addNewBlog(BlogParam blogParam);

    Response<BlogParam> getBlog(BlogParam blogParam);

    Response<List<BlogParam>> listBlogs(BlogParam blogParam);

    Response<List<BlogParam>> listTopBlogs(BlogParam blogParam);

    Response<String> shiftTop(BlogParam blogParam);

    Response<String> deleteBlog(BlogParam blogParam) throws MalformedURLException;

    Response<BlogParam> updateBlog(BlogParam blogParam);

    Response<Integer> getBlogCount();
}
