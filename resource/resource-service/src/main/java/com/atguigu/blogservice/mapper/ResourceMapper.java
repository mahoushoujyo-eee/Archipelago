package com.atguigu.blogservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.atguigu.param.ResourceParam;

import java.util.List;

@Mapper
public interface ResourceMapper
{
    List<ResourceParam> getUserResource(ResourceParam resourceParam);

    void addResource(ResourceParam resourceParam);

    List<ResourceParam> getPublicPanResource(ResourceParam resourceParam);

    Integer getPublicCount();

    Integer getPrivateCount(Long userId);

    void deleteResource(ResourceParam resourceParam);

    List<Long> getPrivateUsage(Long userId);
}

