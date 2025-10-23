package com.atguigu.blogservice.service;

import com.atguigu.blogrpc.param.BlogParam;
import com.atguigu.blogrpc.param.Response;
import com.atguigu.blogrpc.service.BlogRpc;
import com.atguigu.blogservice.mapper.BlogMapper;
import com.atguigu.param.ResourceParam;
import com.atguigu.service.ResourceRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@DubboService
@Slf4j
public class BlogService implements BlogRpc
{
    @Autowired
    private BlogMapper blogMapper;

    @DubboReference(mock = "return {\"code\": 500, \"message\": \"服务降级\"}")
    ResourceRpc resourceRpc;

    private static final String BLOG_BUCKET_NAME = "md-blog";
    private static final String IMG_BUCKET_NAME = "img-blog-5";

    @Override
    public Response<List<URL>> addNewBlog(BlogParam blogParam)
    {
        if(blogParam.getTitle() == null || blogParam.getTitle().isEmpty())
            return Response.error("标题不能为空");

        if(blogParam.getImgName() == null || blogParam.getImgName().isEmpty())
            return Response.error("图片不能为空");

        ResourceParam resourceParam1 = new ResourceParam();
        resourceParam1.setContentType("text/plain");
        resourceParam1.setBucketName(BLOG_BUCKET_NAME);
        resourceParam1.setOssId(blogParam.getTitle() + UUID.randomUUID() + ".md");

        ResourceParam  resourceParam2 = new ResourceParam();
        resourceParam2.setContentType(getContentTypeByFileName(blogParam.getImgName()));
        resourceParam2.setBucketName(IMG_BUCKET_NAME);
        resourceParam2.setOssId(UUID.randomUUID() + blogParam.getImgName());

        // 保持原有的类型声明
        com.atguigu.param.Response<URL> response1 = resourceRpc.getUploadUrl(resourceParam1);
        com.atguigu.param.Response<URL> response2 = resourceRpc.getUploadUrl(resourceParam2);

        // 由于实际返回的是 HashMap，需要进行类型转换
        Object data1 = response1.getData();
        Object data2 = response2.getData();

        URL url1 = transformToURL(data1);
        URL url2 = transformToURL(data2);

        blogParam.setOssId(resourceParam1.getOssId());
        blogParam.setImgUrl(resourceParam2.getOssId());

        blogMapper.addBlog(blogParam);
        return Response.success(List.of(url1, url2));
    }

    @Override
    public Response<BlogParam> getBlog(BlogParam blogParam)
    {
        if(blogParam.getId() == null)
            return Response.error("博客id不能为空");

        BlogParam result = blogMapper.getBlogById(blogParam);
        log.info("result: {}", result);

        ResourceParam resourceParam1 = new ResourceParam();
        resourceParam1.setContentType("text/plain");
        resourceParam1.setBucketName(BLOG_BUCKET_NAME);
        resourceParam1.setOssId(result.getOssId());

        ResourceParam  resourceParam2 = new ResourceParam();
        resourceParam2.setContentType(getContentTypeByFileName(result.getImgUrl()));
        resourceParam2.setBucketName(IMG_BUCKET_NAME);
        resourceParam2.setOssId(result.getImgUrl());

        // 保持原有的类型声明
        com.atguigu.param.Response<URL> response1 = resourceRpc.getDownloadUrl(resourceParam1);
        com.atguigu.param.Response<URL> response2 = resourceRpc.getDownloadUrl(resourceParam2);

        // 由于实际返回的是 HashMap，需要进行类型转换
        Object data1 = response1.getData();
        Object data2 = response2.getData();

        result.setContentUrl(transformToURL(data1).toString());
        result.setImgUrl(transformToURL(data2).toString());

        return Response.success(result);
    }

    @Override
    public Response<List<BlogParam>> listBlogs(BlogParam blogParam)
    {
        if(blogParam.getPageIndex() == null)
            return Response.error("页码不能为空");

        blogParam.setPageOffset((blogParam.getPageIndex()-1)*10);
        List<BlogParam> blogParams = blogMapper.listBlogs(blogParam);
        blogParams.forEach(blog ->
        {
            ResourceParam tempParam = new ResourceParam();
            tempParam.setContentType(getContentTypeByFileName(blog.getImgUrl()));
            tempParam.setBucketName(IMG_BUCKET_NAME);
            tempParam.setOssId(blog.getImgUrl());

            blog.setImgUrl(transformToURL(resourceRpc.getDownloadUrl(tempParam).getData()).toString());
        });
        return Response.success(blogParams);
    }

    @Override
    public Response<List<BlogParam>> listTopBlogs(BlogParam blogParam)
    {
        List<BlogParam> blogParams = blogMapper.listTopBlogs(blogParam);
        blogParams.forEach(blog ->
        {
            ResourceParam tempParam = new ResourceParam();
            tempParam.setContentType(getContentTypeByFileName(blog.getImgUrl()));
            tempParam.setBucketName(IMG_BUCKET_NAME);
            tempParam.setOssId(blog.getImgUrl());

            blog.setImgUrl(transformToURL(resourceRpc.getDownloadUrl(tempParam).getData()).toString());
        });

        return Response.success(blogParams);
    }

    @Override
    public Response<String> shiftTop(BlogParam blogParam)
    {
        if(blogParam.getId() == null)
            return Response.error("博客id不能为空");

        blogMapper.shiftTop(blogParam);
        return Response.success("success");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<String> deleteBlog(BlogParam blogParam) throws MalformedURLException
    {
        log.info("blogParam: {}", blogParam);

        if(blogParam.getId() == null)
            return Response.error("博客id不能为空");

        BlogParam databaseBlog = blogMapper.getBlogById(blogParam);
        blogMapper.deleteBlog(blogParam);
        ResourceParam resourceParam1 = new ResourceParam();
        resourceParam1.setBucketName(BLOG_BUCKET_NAME);
        resourceParam1.setOssId(databaseBlog.getOssId());
        resourceRpc.deleteResource(resourceParam1);

        ResourceParam resourceParam2 = new ResourceParam();
        resourceParam2.setBucketName(IMG_BUCKET_NAME);
        resourceParam2.setOssId(databaseBlog.getImgUrl());
        resourceRpc.deleteResource(resourceParam2);

        return Response.success("success");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<BlogParam> updateBlog(BlogParam blogParam)
    {
        if(blogParam.getId() == null)
            return Response.error("博客id不能为空");

        blogMapper.updateBlog(blogParam);
        BlogParam result = blogMapper.getBlogById(blogParam);

        ResourceParam resourceParam1 = new ResourceParam();
        resourceParam1.setContentType("text/plain");
        resourceParam1.setBucketName(BLOG_BUCKET_NAME);
        resourceParam1.setOssId(result.getOssId());

        ResourceParam  resourceParam2 = new ResourceParam();
        resourceParam2.setContentType(getContentTypeByFileName(result.getImgUrl()));
        resourceParam2.setBucketName(IMG_BUCKET_NAME);
        resourceParam2.setOssId(result.getImgUrl());

        result.setContentUrl(transformToURL(resourceRpc.getUploadUrl(resourceParam1).getData()).toString());
        result.setImgUrl(transformToURL(resourceRpc.getUploadUrl(resourceParam2).getData()).toString());

        return Response.success(result);
    }

    @Override
    public Response<Integer> getBlogCount()
    {
        return Response.success(blogMapper.getBlogCount());
    }

    public URL transformToURL(Object data)
    {
        if (data instanceof Map)
        {
            // 如果是 Map，手动构造 URL
            Map<String, Object> dataMap = (Map<String, Object>) data;
            String protocol = (String) dataMap.get("protocol");
            String host = (String) dataMap.get("host");
            String file = (String) dataMap.get("file");

            String urlStr = protocol + "://" + host + file;

            try
            {
                URL url = new URL(urlStr);
                log.info("URL: {}", url);
                return url ;
            }
            catch (Exception e)
            {
                log.error("构造URL失败: {}", e.getMessage());
                return null;
            }
        }
        else if (data instanceof URL)
        {
            // 如果是 URL，直接返回
            URL url = (URL) data;
            log.info("URL: {}", url);
            return url;
        }
        else
        {
            log.error("未知的数据类型: {}", data.getClass());
            return null;
        }
    }

    public String getContentTypeByFileName(String fileName)
    {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        switch (extension)
        {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "bmp":
                return "image/bmp";
            case "svg":
                return "image/svg+xml";
            case "ico":
                return "image/x-icon";
            default:
                return "application/octet-stream";
        }
    }
}
