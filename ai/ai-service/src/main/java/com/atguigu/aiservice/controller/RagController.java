package com.atguigu.aiservice.controller;

import com.atguigu.aiservice.param.FileParam;
import com.atguigu.aiservice.param.RagParam;
import com.atguigu.aiservice.param.Response;
import com.atguigu.aiservice.service.RagService;
import com.atguigu.aiservice.service.VectorStoreService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController
{
    @Autowired
    RagService ragService;

    @PostMapping("/list")
    public Response<List<RagParam>> list(@RequestBody RagParam param)
    {
        return ragService.list(param);
    }

    @PostMapping("/create")
    public Response<RagParam> create(@RequestBody RagParam param)
    {
        return ragService.create(param);
    }

    @PostMapping("/delete")
    public Response<RagParam> delete(@RequestBody RagParam param)
    {
        return ragService.delete(param);
    }

    @PostMapping("/update")
    public Response<RagParam> update(@RequestBody RagParam param)
    {
        return ragService.update(param);
    }

    @PostMapping("/get-urls")
    public Response<List<String>> getUploadUrls(@RequestBody List<FileParam> param)
    {
        return ragService.getUploadUrls(param);
    }

    @PostMapping("/finish-upload")
    public Response<String> finishUpload(@RequestBody RagParam param)
    {
        return ragService.getAllUrlsAndLoad(param);
    }



    @Autowired
    VectorStoreService vectorStoreService;
    @GetMapping("/add")
    public void add(@RequestParam("url") String url)
    {
        RagParam ragParam = new RagParam();
        ragParam.setCollectionName("kb_9_1753430035381");
        ragParam.setUserId(1L);

        TikaDocumentReader textReader = new TikaDocumentReader(url);
        // 您可以添加自定义元数据，例如文件名等
        List<Document> documents = textReader.read();
        vectorStoreService.addDocument(ragParam, documents);
    }
}
