package com.atguigu.aiservice;

import com.atguigu.aiservice.param.RagParam;
import com.atguigu.aiservice.service.VectorStoreService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AiServiceApplicationTests {

    @Autowired
    private VectorStoreService vectorStoreService;

    @Test
    void contextLoads()
    {
//        String url = "https://www.baidu.com";
//
//        RagParam ragParam = new RagParam();
//        ragParam.setCollectionName("kb_9_1753430035381");
//        ragParam.setUserId(1L);
//
//        TextReader textReader = new TextReader(url);
//        // 您可以添加自定义元数据，例如文件名等
//        textReader.getCustomMetadata().put("source_url", url);
//        List<Document> documents = textReader.read();
//        vectorStoreService.addDocument(ragParam, documents);
    }

}
