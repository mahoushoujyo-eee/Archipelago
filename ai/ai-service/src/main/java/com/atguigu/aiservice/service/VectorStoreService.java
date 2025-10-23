package com.atguigu.aiservice.service;

import com.atguigu.aiservice.param.RagParam;
import com.atguigu.aiservice.spliter.MySplitter;
import com.google.common.collect.Lists;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.constant.MilvusClientConstant;
import io.milvus.grpc.CreateIndexRequest;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.v2.common.IndexParam;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.transformer.ContentFormatTransformer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.text.DefaultFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class VectorStoreService
{
    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private MySplitter mySplitter;


    private MilvusServiceClient milvusServiceClient;

    @Value("${spring.ai.vectorstore.milvus.client.host:localhost}")
    private String milvusHost;

    @Value("${spring.ai.vectorstore.milvus.client.port:19530}")
    private int milvusPort;

    @PostConstruct
    public void init()
    {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(milvusHost)
                .withPort(milvusPort)
                .build();
        milvusServiceClient = new MilvusServiceClient(connectParam);
    }



    public MilvusVectorStore getVectorStore(RagParam param)
    {

        MilvusVectorStore milvusVectorStore = MilvusVectorStore.builder(milvusServiceClient, embeddingModel)
                .databaseName("default")
                .collectionName(param.getCollectionName())
                .initializeSchema(true)
                .metricType(MetricType.COSINE)
                .indexType(IndexType.IVF_FLAT)
                .indexParameters("{\"nlist\":1024}")
                .embeddingDimension(1536)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
        return milvusVectorStore;
    }

    public void deleteCollection(RagParam param)
    {
        DropCollectionParam dropCollectionParam = DropCollectionParam.newBuilder()
                .withCollectionName(param.getCollectionName())
                .build();
        milvusServiceClient.dropCollection(dropCollectionParam);
    }

    public void createCollection(RagParam param)
    {
        FieldType docId = FieldType.newBuilder()
                .withName("doc_id")
                .withDataType(DataType.VarChar)
                .withMaxLength(36)
                .withPrimaryKey(true)
                .build();

        FieldType content = FieldType.newBuilder()
                .withName("content")
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();

        FieldType metadata = FieldType.newBuilder()
                .withName("metadata")
                .withDataType(DataType.JSON)
                .build();

        FieldType embedding = FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(1536)
                .build();

        CollectionSchemaParam collectionSchemaParam = CollectionSchemaParam.newBuilder()
                .withEnableDynamicField(false)
                .withFieldTypes(List.of(docId, content, metadata, embedding))
                .build();

        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(param.getCollectionName())
                .withDatabaseName("default")
                .withSchema(collectionSchemaParam)
                .build();
        milvusServiceClient.createCollection(createCollectionParam);

        CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                .withCollectionName(param.getCollectionName())
                .withFieldName("embedding")
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withExtraParam("{\"nlist\":1024}")
                .build();

        milvusServiceClient.createIndex(indexParam);

        milvusServiceClient.loadCollection(
            LoadCollectionParam.newBuilder()
                    .withCollectionName(param.getCollectionName())
                    .build()
        );
    }

    public void addDocument(RagParam param, List<Document> documents) {
        try
        {
//            ContentFormatter contentFormatter = DefaultContentFormatter.defaultConfig();
//            ContentFormatTransformer contentFormatTransformer = new ContentFormatTransformer(contentFormatter);
//            documents = contentFormatTransformer.transform(documents);

//             使用 Spring AI 的 TokenTextSplitter 进行文档分割
            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(512)                 // 每段最大 token 数（OpenAI 建议 512~800）
                    .withMinChunkSizeChars(50)          // 字符级下限，防碎片
                    .withMinChunkLengthToEmbed(50)      // 嵌入前再过滤一次超短段
                    .withMaxNumChunks(1000)             // 单文档最多段数，防 OOM
                    .withKeepSeparator(true)            // 保留换行/标题，语义更完整
                    .build();
            // 分割文档
            List<Document> splitDocuments = splitter.apply(documents);

//            List<Document> splitDocuments = mySplitter.semanticChunk(documents);

            log.info("splitDocuments size: {}", splitDocuments.size());
            // 获取向量存储并添加文档
            MilvusVectorStore vectorStore = getVectorStore(param);
            log.info("添加文档到VectorDatabase");
            Lists.partition(splitDocuments, 10)   // Guava 工具
                    .forEach(vectorStore::add);

        }
        catch (Exception e)
        {
            log.error("Error adding documents to vector store", e);
            throw new RuntimeException("Failed to add documents: " + e.getMessage(), e);
        }
    }


    private String generateValidUUID(String userId) {
        try
        {
            // 如果已经是有效UUID，直接返回
            return UUID.fromString(userId).toString();
        } catch (IllegalArgumentException e) {
            // 如果不是UUID，基于userId生成一个确定性的UUID
            return UUID.nameUUIDFromBytes(userId.getBytes()).toString();
        }
    }

}
