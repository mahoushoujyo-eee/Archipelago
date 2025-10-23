package com.atguigu.blogservice.service;

import com.atguigu.blogrpc.param.CommentParam;
import com.atguigu.blogrpc.param.Response;
import com.atguigu.blogrpc.service.CommentRpc;
import com.atguigu.blogservice.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService implements CommentRpc
{
    @Autowired
    private CommentMapper commentMapper;

    public Response<String> addComment(CommentParam commentParam)
    {
        if(commentParam.getBlogId() == null)
            return Response.error("博客id不能为空");

        if(commentParam.getUserId() == null)
            return Response.error("用户id不能为空");

        if(commentParam.getContent() == null || commentParam.getContent().isEmpty())
            return Response.error("评论内容不能为空");

        commentMapper.addComment(commentParam);
        return Response.success("添加成功");
    }

    public Response<String> deleteComment(CommentParam commentParam)
    {
        if(commentParam.getId() == null)
            return Response.error("评论id不能为空");

        commentMapper.deleteComment(commentParam);
        return Response.success("删除成功");
    }

    public Response<String> addCommentTop(CommentParam commentParam)
    {
        if(commentParam.getId() == null)
            return Response.error("评论id不能为空");

        commentMapper.addCommentTop(commentParam);
        return Response.success("添加成功");
    }

    public Response<String> cancelCommentTop(CommentParam commentParam)
    {
        if(commentParam.getId() == null)
            return Response.error("评论id不能为空");

        commentMapper.cancelCommentTop(commentParam);
        return Response.success("取消成功");
    }

    public Response<List<CommentParam>> listComments(CommentParam commentParam)
    {
        if(commentParam.getBlogId() == null)
            return Response.error("博客id不能为空");

        commentParam.setPageOffset((commentParam.getPageIndex()-1)*10);
        return Response.success(commentMapper.listComments(commentParam));
    }

    public Response<Integer> getCommentCount(CommentParam commentParam)
    {
        if(commentParam.getBlogId() == null)
            return Response.error("博客id不能为空");

        return Response.success(commentMapper.getCommentCount(commentParam));
    }
}
