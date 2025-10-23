package com.atguigu.aiservice.mapper;

import com.atguigu.aiservice.param.RagParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RagMapper
{
    List<RagParam> list(RagParam param);

    void create(RagParam param);

    void delete(RagParam param);

    void update(RagParam param);
}
