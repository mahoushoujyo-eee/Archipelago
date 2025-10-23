package com.atguigu.blogservice.mapper;

import com.atguigu.blogrpc.param.CommentParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper
{
    void addComment(CommentParam commentParam);

    List<CommentParam> listComments(CommentParam commentParam);

    void addCommentTop(CommentParam commentParam);

    void cancelCommentTop(CommentParam commentParam);

    void deleteComment(CommentParam commentParam);

    Integer getCommentCount(CommentParam commentParam);
}
