package com.atguigu.aiservice.controller;

import com.atguigu.aiservice.entity.Conversation;
import com.atguigu.aiservice.entity.Model;
import com.atguigu.aiservice.param.Response;
import com.atguigu.aiservice.service.ChatService;
import com.atguigu.aiservice.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/model")
public class InfoController
{
    @Autowired
    private ChatService chatService;

    @GetMapping("/list")
    public Response<List<Model>> list()
    {
        return Response.success(chatService.getModels());
    }


    //历史聊天记录接口
    @GetMapping("/history/{userId}")
    public Response<List<Conversation>> history(@PathVariable("userId") Long userId)
    {
        return chatService.getHistory(userId);
    }

    @GetMapping("/history-conversation/{conversationId}")
    public Response<List<Message>> history(@PathVariable("conversationId") String conversationId)
    {
        return chatService.getHistoryConversation(conversationId);
    }

    @DeleteMapping("/history-conversation/{conversationId}")
    public Response<String> deleteHistory(@PathVariable("conversationId") String conversationId)
    {
        return chatService.deleteHistory(conversationId);
    }

    //
}
