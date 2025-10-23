package com.atguigu.aiservice.controller;

import com.atguigu.aiservice.param.ChatParam;
import com.atguigu.aiservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController
{
    @Autowired
    private ChatService chatService;

    @PostMapping(value= "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(@RequestBody ChatParam param)
    {
        return chatService.chat(param);
    }
}
