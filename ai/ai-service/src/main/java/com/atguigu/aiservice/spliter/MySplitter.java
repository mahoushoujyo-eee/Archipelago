package com.atguigu.aiservice.spliter;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
public class MySplitter
{
    @Autowired
    private EmbeddingModel embeddingModel;

    private static final double SIMILARITY_THRESHOLD = 0.6;

    private TokenTextSplitter splitter = new TokenTextSplitter(512, 0, 5, 1000, true);

    /**
     * 语义分块：输入已切好的小块 Document，输出语义合并后的大块 Document
     */
    public List<Document> semanticChunk(List<Document> docs)
    {
        if (docs == null || docs.isEmpty())
            return List.of();

        docs = splitter.apply(docs);

        // 1. 计算所有小块的 embedding
        List<float[]> vectors = embed(docs);

        // 2. 计算相邻块相似度，找到断点
        List<Integer> breakpoints = new ArrayList<>();
        for (int i = 0; i < vectors.size() - 1; i++)
        {
            double sim = cosine(vectors.get(i), vectors.get(i + 1));
            if (sim < SIMILARITY_THRESHOLD)
                breakpoints.add(i);
        }

        // 3. 根据断点把原始 docs 分组，再拼接
        List<Document> result = new ArrayList<>();
        int start = 0;
        for (int end : breakpoints)
        {
            result.add(mergeAsDocument(docs, start, end));
            start = end + 1;
        }
        result.add(mergeAsDocument(docs, start, docs.size() - 1));
        return result;
    }

    /* ------------ 新增工具方法 ------------ */

    private Document mergeAsDocument(List<Document> docs, int start, int endInclusive)
    {
        StringBuilder content = new StringBuilder();
        Map<String, Object> mergedMeta = new HashMap<>();

        // 把同一段落内的块按原文顺序拼接
        for (int i = start; i <= endInclusive; i++)
        {
            Document d = docs.get(i);
            content.append(d.getText());
            // 如有需要，把各块的元数据也合并（简单覆盖，或自定义策略）
            mergedMeta.putAll(d.getMetadata());
        }

        // 额外存一下段落范围等信息
        mergedMeta.put("chunk_start_index", start);
        mergedMeta.put("chunk_end_index", endInclusive);

        return new Document(content.toString(), mergedMeta);
    }

    /* ------------ 工具方法 ------------ */

    private List<float[]> embed(List<Document> docs)
    {
        List<String> texts = docs.stream().map(Document::getText).toList();

        // 分批处理，每批最多10个元素
        return partition(texts, 10).stream()
                .flatMap(batch ->
                {
                    EmbeddingResponse resp = embeddingModel.embedForResponse(batch);
                    return resp.getResults().stream().map(Embedding::getOutput);
                })
                .toList();
    }

    private double cosine(float[] a, float[] b)
    {
        double dot = IntStream.range(0, a.length).mapToDouble(i -> a[i] * b[i]).sum();
        double normA = Math.sqrt(IntStream.range(0, a.length).mapToDouble(i -> a[i] * a[i]).sum());
        double normB = Math.sqrt(IntStream.range(0, b.length).mapToDouble(i -> b[i] * b[i]).sum());
        return dot / (normA * normB);
    }

    // 辅助方法：将列表分批
    private <T> List<List<T>> partition(List<T> list, int batchSize)
    {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize)
        {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
}
