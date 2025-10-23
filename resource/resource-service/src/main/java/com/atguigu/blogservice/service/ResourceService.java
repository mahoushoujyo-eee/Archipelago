package com.atguigu.blogservice.service;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.atguigu.blogservice.mapper.ResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.atguigu.param.ResourceParam;
import com.atguigu.param.Response;
import com.atguigu.service.ResourceRpc;

import java.net.URL;
import java.util.*;

@Service
@DubboService
@Slf4j
public class ResourceService implements ResourceRpc
{
    @Autowired
    private OSS ossClient;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Response<URL> getUploadUrl(ResourceParam resourceParam)
    {
        // 设置请求头。
        Map<String, String> headers = new HashMap<>();
        // 指定ContentType。
        headers.put(OSSHeaders.CONTENT_TYPE, resourceParam.getContentType());
        // 指定生成的预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
        // 生成预签名URL。
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(resourceParam.getBucketName(), resourceParam.getOssId(), HttpMethod.PUT);
        // 设置过期时间。
        request.setExpiration(expiration);
        request.setHeaders(headers);
        // 通过HTTP PUT请求生成预签名URL。
        URL signedUrl = ossClient.generatePresignedUrl(request);
        // 打印预签名URL。
        log.info("Generated presigned URL: {}", signedUrl);
        return Response.success(signedUrl);
    }

    @Override
    public Response<URL> getDownloadUrl(ResourceParam resourceParam)
    {
        if (resourceParam.getBucketName() == null) resourceParam.setBucketName("resource-5");

        // 设置预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
        URL url = ossClient.generatePresignedUrl(resourceParam.getBucketName(), resourceParam.getOssId(), expiration);

        return Response.success(url);
    }

    @Override
    public Response<List<ResourceParam>> getPanUserResource(ResourceParam resourceParam)
    {
        resourceParam.setPageOffset((resourceParam.getPageIndex()-1)*10);

        return Response.success(resourceMapper.getUserResource(resourceParam));
    }

    @Override
    public Response<List<ResourceParam>> getBlogUserResource(Long userId)
    {
        return null;
    }

    @Override
    public Response<List<ResourceParam>> getPanPublicResource(ResourceParam resourceParam)
    {
        resourceParam.setPageOffset((resourceParam.getPageIndex()-1)*10);
        return Response.success(resourceMapper.getPublicPanResource(resourceParam));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> deleteResource(ResourceParam resourceParam)
    {
        resourceMapper.deleteResource(resourceParam);
        ossClient.deleteObject(resourceParam.getBucketName(), resourceParam.getOssId());
        return Response.success("删除成功");
    }

    @Override
    public Response<URL> addResource(ResourceParam resourceParam)
    {
        return null;
    }

    @Override
    public Response<URL> addPanResource(ResourceParam resourceParam)
    {
        if (resourceParam.getBucketName() == null)
            return Response.error(400, "bucketName不能为空");

        if (resourceParam.getObjectName() == null)
            return Response.error(400, "objectName不能为空");

        resourceParam.setOssId(UUID.randomUUID()+resourceParam.getObjectName());
        resourceMapper.addResource(resourceParam);
        return getUploadUrl(resourceParam);
    }

    @Override
    public Response<URL> addBlogResource(ResourceParam resourceParam)
    {
        return null;
    }

    @Override
    public Response<Integer> getPublicCount()
    {
        return Response.success(resourceMapper.getPublicCount());
    }

    @Override
    public Response<Integer> getPrivateCount(ResourceParam resourceParam)
    {
        return Response.success(resourceMapper.getPrivateCount(resourceParam.getUserId()));
    }

    @Override
    public Response<Long> getPrivateUsage(ResourceParam resourceParam) {
        if (resourceParam.getUserId() == null)
            return Response.error(400, "userId不能为空");

        List<Long> privateUsages = resourceMapper.getPrivateUsage(resourceParam.getUserId());

        log.info("privateUsages: {}", privateUsages);
        log.info("privateUsages.size(): {}", privateUsages.size());
        if (privateUsages.get(0) == null)
            return Response.success(0L);

        Long result = privateUsages.stream().reduce(0L, Long::sum);
        return  Response.success(result);
    }

    @Override
    public Response<String> deleteBlogResource(ResourceParam resourceParam)
    {
        if (resourceParam.getBucketName() == null)
            return Response.error(400, "bucketName不能为空");

        if (resourceParam.getOssId() == null)
            return Response.error(400, "ossId不能为空");

        ossClient.deleteObject(resourceParam.getBucketName(), resourceParam.getOssId());
        return Response.success("删除成功");
    }
}
