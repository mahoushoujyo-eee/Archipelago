package com.atguigu.blogservice.mapper;

import com.atguigu.blogrpc.param.BlogParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMapper
{
    void addBlog(BlogParam blogParam);

    List<BlogParam> listBlogs(BlogParam blogParam);

    BlogParam getBlogById(BlogParam blogParam);

    List<BlogParam> listTopBlogs(BlogParam blogParam);

    void shiftTop(BlogParam blogParam);

    void deleteBlog(BlogParam blogParam);

    void updateBlog(BlogParam blogParam);

    Integer getBlogCount();
}
