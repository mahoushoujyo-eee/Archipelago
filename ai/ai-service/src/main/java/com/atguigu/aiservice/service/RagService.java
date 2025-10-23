package com.atguigu.aiservice.service;

import com.atguigu.aiservice.mapper.RagMapper;
import com.atguigu.aiservice.param.FileParam;
import com.atguigu.aiservice.param.RagParam;
import com.atguigu.aiservice.param.Response;
import com.atguigu.param.ResourceParam;
import com.atguigu.service.ResourceRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@DubboService
public class RagService
{
    @Autowired
    RagMapper ragMapper;

    @Autowired
    VectorStoreService vectorStoreService;

    @DubboReference(mock = "return {\"code\": 500, \"message\": \"服务降级\"}")
    ResourceRpc resourceRpc;

    private final String BUCKET_NAME = "rag-blog";

    public Response<List<RagParam>> list(RagParam param)
    {
        log.info("list param: {}", param);
        List<RagParam> list = ragMapper.list(param);
        log.info("list result: {}", list);
        return Response.success(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<RagParam> create(RagParam param)
    {
        param.setCollectionName("kb_" + param.getUserId() + "_" + (new Date().getTime()));
        ragMapper.create(param);
        vectorStoreService.createCollection(param);

        return Response.success(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<RagParam> delete(RagParam param)
    {
        ragMapper.delete(param);
        vectorStoreService.deleteCollection(param);

        return Response.success(param);
    }

    public Response<RagParam> update(RagParam param)
    {
        ragMapper.update(param);

        return Response.success(param);
    }

    public Response<List<String>> getUploadUrls(List<FileParam> param)
    {
        List<String> urls = param.stream().map(fileParam ->
        {
            ResourceParam resourceParam = new ResourceParam();
            resourceParam.setContentType(fileParam.getContentType());
            resourceParam.setOssId(fileParam.getUserId() + "_" + fileParam.getName());
            resourceParam.setBucketName(BUCKET_NAME);

            return transformToURL(resourceRpc.getUploadUrl(resourceParam).getData()).toString();
        }).collect(Collectors.toList());
        return Response.success(urls);
    }

    public Response<String> getAllUrlsAndLoad(RagParam param)
    {
        List<String> urls = param.getFiles().stream().map(fileParam ->
        {
            if(fileParam.getUrl() == null)
            {
                ResourceParam resourceParam = new ResourceParam();
                resourceParam.setOssId(fileParam.getUserId() + "_" + fileParam.getName());
                resourceParam.setBucketName(BUCKET_NAME);
                resourceParam.setContentType(fileParam.getContentType());
                String url = transformToURL(resourceRpc.getDownloadUrl(resourceParam).getData()).toString();

                fileParam.setUrl(url);
            }

            return fileParam.getUrl();
        }).collect(Collectors.toList());

        // 使用 CompletableFuture 异步执行
        CompletableFuture.supplyAsync(() ->
        {
            addDocumentWithUrl(param, urls);
            log.info("文档处理完成: {}", param.getCollectionName());
            return "success";
        }).exceptionally(ex ->
        {
            log.error("文档处理失败: {}", ex.getMessage());
            return "error";
        });

        return Response.success("正在将文档载入知识库");
    }

    public Response<String> addDocumentWithUrl(RagParam param, List<String> urls)
    {
        urls.stream().forEach(url ->
        {
            TikaDocumentReader reader = new TikaDocumentReader(url);
            List<Document> documents = reader.get();
            vectorStoreService.addDocument(param, documents);
        });

        return Response.success("success");
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


}
