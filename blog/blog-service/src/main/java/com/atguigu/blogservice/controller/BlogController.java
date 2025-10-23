package com.atguigu.blogservice.controller;

import com.atguigu.blogrpc.param.BlogParam;
import com.atguigu.blogrpc.param.Response;
import com.atguigu.blogservice.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController
{
    @Autowired
    private BlogService blogService;

    @PostMapping("/add")
    public Response<List<URL>> getUrl(@RequestBody BlogParam blogParam)
    {
        return blogService.addNewBlog(blogParam);
    }

    @PostMapping("/list")
    public Response<List<BlogParam>> listBlogs(@RequestBody BlogParam blogParam)
    {
        return blogService.listBlogs(blogParam);
    }

    @PostMapping("/get")
    public Response<BlogParam> getBlog(@RequestBody BlogParam blogParam)
    {
        return blogService.getBlog(blogParam);
    }

    @PostMapping("/delete")
    public Response<String> deleteBlog(@RequestBody BlogParam blogParam) throws MalformedURLException
    {
        return blogService.deleteBlog(blogParam);
    }

    @PostMapping("/update")
    public Response<BlogParam> updateBlog(@RequestBody BlogParam blogParam)
    {
        return blogService.updateBlog(blogParam);
    }

    @PostMapping("/list-top")
    public Response<List<BlogParam>> listTopBlogs(@RequestBody BlogParam blogParam)
    {
        return blogService.listTopBlogs(blogParam);
    }

    @PostMapping("/shift-top")
    public Response<String> shiftTop(@RequestBody BlogParam blogParam)
    {
        return blogService.shiftTop(blogParam);
    }

    @GetMapping("/get-count")
    public Response<Integer> getBlogCount()
    {
        return blogService.getBlogCount();
    }


}
