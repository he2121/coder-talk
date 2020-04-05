package com.ganghuan;

import com.ganghuan.controller.BlogController;
import com.ganghuan.dto.CommentVo;
import com.ganghuan.pojo.Comment;
import com.ganghuan.service.CommentService;
import com.ganghuan.util.ConstantUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CoderCircleApplicationTests {

    @Autowired
    private BlogController blogController;
    @Autowired
    private CommentService commentService;

    @Test
    void contextLoads() {
        List<Comment> comments = commentService.findCommentsByEntity(ConstantUtil.ENTITY_TYPE_BLOG,16,0,100);
        List<CommentVo> commentsVoFromComments = blogController.getCommentsVoFromComments(comments);
        System.out.println(commentsVoFromComments);
    }

}
