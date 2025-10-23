package com.atguigu.blogservice.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.Bucket;
import com.atguigu.blogservice.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.atguigu.param.ResourceParam;
import com.atguigu.param.Response;

import java.net.URL;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/oss")
public class ResourceController
{
    @Autowired
    OSS ossClient;

    @Autowired
    ResourceService resourceService;

    @GetMapping("/list")
    public String list()
    {
        List<Bucket> buckets = ossClient.listBuckets();

        StringBuffer sb = new StringBuffer();
        for (Bucket bucket : buckets) {
            sb.append("\n").append(bucket.getName());
        }

        return sb.toString();
    }

    @GetMapping("/signature")
    public Response<URL> signature(@RequestParam("obj") String objectName)
    {
        return null;
    }

    @PostMapping("/add")
    public Response<URL> add(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.addPanResource(resourceParam);
    }

    @PostMapping("/public/list")
    public Response<List<ResourceParam>> listPublic(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.getPanPublicResource(resourceParam);
    }

    @PostMapping("/private/list")
    public Response<List<ResourceParam>> listPrivate(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.getPanUserResource(resourceParam);
    }

    @GetMapping("/public/count")
    public Response<Integer> getPublicCount()
    {
        return resourceService.getPublicCount();
    }

    @PostMapping("/delete")
    public Response<String> delete(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.deleteResource(resourceParam);
    }

    @PostMapping("/private/count")
    public Response<Integer> getPrivateCount(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.getPrivateCount(resourceParam);
    }

    @PostMapping("/private/usage")
    public Response<Long> getPrivateUsage(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.getPrivateUsage(resourceParam);
    }

    @PostMapping("/public/download/url")
    public Response<URL> getDownloadUrl(@RequestBody ResourceParam resourceParam)
    {
        return resourceService.getDownloadUrl(resourceParam);
    }


}
