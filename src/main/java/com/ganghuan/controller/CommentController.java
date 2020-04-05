package com.ganghuan.controller;


import com.ganghuan.dto.LoginHold;
import com.ganghuan.pojo.Comment;
import com.ganghuan.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    private LoginHold loginHold;

    @PostMapping("/add/{id}")
    public String addComment(@PathVariable("id") int id, Comment comment){
        comment.setUserId(loginHold.getUser().getId());

        commentService.addComment(comment);

        return "redirect:/blog/detail/"+id;
    }
}
