package com.atguigu.service;



import com.atguigu.param.ResourceParam;
import com.atguigu.param.Response;

import java.net.URL;
import java.util.List;

public interface ResourceRpc
{
    public Response<URL> getUploadUrl(ResourceParam resourceParam);

    public Response<URL> getDownloadUrl(ResourceParam resourceParam);

    public Response<List<ResourceParam>> getPanUserResource(ResourceParam resourceParam);
    public Response<List<ResourceParam>> getBlogUserResource(Long userId);
    public Response<List<ResourceParam>> getPanPublicResource(ResourceParam resourceParam);

    public Response<String> deleteResource(ResourceParam resourceParam);

    public Response<URL> addResource(ResourceParam resourceParam);
    public Response<URL> addPanResource(ResourceParam resourceParam);
    public Response<URL> addBlogResource(ResourceParam resourceParam);

    public Response<Integer> getPublicCount();
    public Response<Integer> getPrivateCount(ResourceParam resourceParam);

    public Response<Long> getPrivateUsage(ResourceParam resourceParam);

    public Response<String> deleteBlogResource(ResourceParam resourceParam);
}
