package com.atguigu.blogservice.controller;

import com.atguigu.blogrpc.param.CommentParam;
import com.atguigu.blogrpc.param.Response;
import com.atguigu.blogservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public Response<String> addComment(@RequestBody CommentParam commentParam)
    {
        return commentService.addComment(commentParam);
    }

    @PostMapping("/delete")
    public Response<String> deleteComment(@RequestBody CommentParam commentParam)
    {
        return commentService.deleteComment(commentParam);
    }

    @PostMapping("/add-top")
    public Response<String> addCommentTop(@RequestBody CommentParam commentParam)
    {
        return commentService.addCommentTop(commentParam);
    }

    @PostMapping("/cancel-top")
    public Response<String> cancelCommentTop(@RequestBody CommentParam commentParam) {
        return commentService.cancelCommentTop(commentParam);
    }

    @PostMapping("/list")
    public Response<List<CommentParam>> listComments(@RequestBody CommentParam commentParam)
    {
        return commentService.listComments(commentParam);
    }

    @PostMapping("/get-count")
    public Response<Integer> getCommentCount(@RequestBody CommentParam commentParam)
    {
        return commentService.getCommentCount(commentParam);
    }
}
