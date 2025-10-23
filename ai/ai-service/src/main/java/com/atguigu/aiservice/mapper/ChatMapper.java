package com.atguigu.aiservice.mapper;

import com.atguigu.aiservice.entity.Conversation;
import com.atguigu.aiservice.entity.Message;
import com.atguigu.aiservice.entity.Model;
import com.atguigu.aiservice.param.ChatParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper
{
    void addNewConversation(ChatParam param);

    boolean ifConversationExist(Integer conversationId);

    List<Model> getModelList();

    List<Conversation> getConversationList(Long userId);

    List<Message> getConversation(String conversationId);

    void deleteHistory(String conversationId);
}
