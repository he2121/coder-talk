package com.ganghuan.controller;


import com.ganghuan.dto.CommentVo;
import com.ganghuan.dto.LoginHold;
import com.ganghuan.dto.Page;
import com.ganghuan.pojo.Blog;
import com.ganghuan.pojo.Comment;
import com.ganghuan.pojo.User;
import com.ganghuan.service.BlogService;
import com.ganghuan.service.CommentService;
import com.ganghuan.service.UserService;
import com.ganghuan.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.*;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LoginHold loginHold;


    @GetMapping("/add")
    public String addBlog(){
        return "/editblog";
    }

    @PostMapping("/add")
    public String editBlog(String title, String content, Model model){
        User user = loginHold.getUser();
        if(user == null){
            model.addAttribute("msg","用户登录状态异常，请重新登录");
            model.addAttribute("target","/login");
            return "/common/jump";
        }

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setUserId(user.getId());
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());

        model.addAttribute("target","/index");
        model.addAttribute("msg","发布成功，正在跳往首页");
        blogService.addBlog(blog);
        return "/common/jump";
    }

    @GetMapping("/detail/{id}")
    public String getDetailBlog(@PathVariable("id") int blogId, Model model, Page page){


        //添加博客信息
        Blog blog = blogService.findBlogById(blogId);
        model.addAttribute("blog",blog);

        //添加作者信息
        User user = userService.findUserById(blog.getUserId());
        model.addAttribute("user",user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/blog/detail/" + blogId);
        page.setRows(blog.getCommentCount());

        // 添加评论，回复
        List<Comment> comments = commentService.findCommentsByEntity(ConstantUtil.ENTITY_TYPE_BLOG,blogId,page.getOffset(),page.getLimit());
        List<CommentVo> commentsVo = getCommentsVoFromComments(comments);
        model.addAttribute("commentsVo",commentsVo);
        return "/detail";
    }

    public List<CommentVo> getCommentsVoFromComments(List<Comment> comments) {
        List<CommentVo> commentsVo = new ArrayList<>();
        if (comments != null){
            for (Comment comment:comments){
                CommentVo commentVo = new CommentVo();
                // 评论
                commentVo.setComment(comment);
                // 作者
                commentVo.setUser(userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                List<CommentVo> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply:replyList){
                        CommentVo replyVo = new CommentVo();
                        replyVo.setComment(reply);
                        replyVo.setUser(userService.findUserById(reply.getUserId()));
                        replyVo.setReplyVoList(new ArrayList<>());
                        //回复专用
                        replyVo.setTargetName(userService.findUserById(reply.getTargetId()).getUsername());
                        replyVoList.add(replyVo);
                    }
                    commentVo.setReplyVoList(replyVoList);
                    commentsVo.add(commentVo);
                }

            }
        }

        return commentsVo;
    }
}
