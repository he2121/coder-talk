package com.ganghuan.service;


import com.ganghuan.dto.CommentVo;
import com.ganghuan.mapper.BlogMapper;
import com.ganghuan.mapper.CommentMapper;
import com.ganghuan.pojo.Comment;
import com.ganghuan.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {


    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

    @Transactional
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        comment.setCreateTime(new Date());
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ConstantUtil.ENTITY_TYPE_BLOG) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            blogMapper.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }



}
