package com.ganghuan.controller;


import com.ganghuan.dto.LoginHold;
import com.ganghuan.event.EventProducer;
import com.ganghuan.pojo.Blog;
import com.ganghuan.pojo.Comment;
import com.ganghuan.pojo.Event;
import com.ganghuan.service.BlogService;
import com.ganghuan.service.CommentService;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/comment")
@Transactional
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;

    @Autowired
    private LoginHold loginHold;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/add/{id}")
    public String addComment(@PathVariable("id") int id, Comment comment){
        if (loginHold.getUser() == null) throw new RuntimeException("用户没有登录！");
        comment.setUserId(loginHold.getUser().getId());

        commentService.addComment(comment);
        // 触发评论事件
        Event event = new Event().setTopic(ConstantUtil.TOPIC_COMMENT).setUserId(loginHold.getUser().getId())
                .setEntityType(comment.getEntityType()).setEntityId(comment.getId())
                .setData("blogId",id);
        if (comment.getEntityType() == ConstantUtil.ENTITY_TYPE_BLOG){
            Blog blogById = blogService.findBlogById(comment.getEntityId());
            event.setEntityUserId(blogById.getUserId());
        }else if(comment.getEntityType() == ConstantUtil.ENTITY_TYPE_COMMENT){
            Comment commentById = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(commentById.getUserId());
        }
        eventProducer.fireEvent(event);

        //热度
        redisTemplate.opsForZSet().incrementScore(ConstantUtil.PREFIX_HOTBLOG, String.valueOf(id),5);

        return "redirect:/blog/detail/"+id;
    }

    @PostMapping("/addUserComment/{id}")
    public String addUerComment(@PathVariable("id") int id, Comment comment, Model model){

        comment.setUserId(loginHold.getUser().getId());

        commentService.addComment(comment);
        return "redirect:/user/commentboard/"+id;
    }
}
