package com.ganghuan.controller;

import com.ganghuan.dto.Page;
import com.ganghuan.pojo.Blog;
import com.ganghuan.pojo.User;
import com.ganghuan.service.*;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.JsonUtil;
import com.ganghuan.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class indexController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ViewCountService viewCountService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping({"/","/index"})
    public String index(Model model, Page page){
        return indexHelper(0,model,page);
    }
    @GetMapping({"/{id}","/index{id}"})
    public String index(@PathVariable("id")Integer id,Model model, Page page){
        return indexHelper(id,model,page);
    }

    public String indexHelper(Integer id,Model model, Page page){

        page.setRows(blogService.findBlogsRows(id));
        String path = "/"+id;
        page.setPath(path);


        List<Blog> blogs = blogService.findBlogs(id, page.getOffset(), page.getLimit());

        List<Map<String,Object>> maps = getMaps(blogs);
        model.addAttribute("maps", maps);
        // 今日热帖
        Set<String> hot = redisTemplate.opsForZSet().reverseRange(ConstantUtil.PREFIX_HOTBLOG, 0, 4);
        List<Blog> hotBlogs = new ArrayList<>();
        for (String s : hot) {
            hotBlogs.add(blogService.findBlogById(Integer.parseInt(s)));
        }
        model.addAttribute("hotBlogs",hotBlogs);
        return "index";
    }

    @GetMapping("/circle/{userId}")
    public String circleBlogs(@PathVariable("userId") int userId,Model model,Page page){
        page.setRows(blogService.findCircleBlogsRows(userId));
        String path = "/circle"+userId;
        page.setPath(path);

        List<Blog> blogs = blogService.findCircleBlogs(userId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> maps = getMaps(blogs);
        model.addAttribute("maps", maps);
        return "index";
    }


    private List<Map<String,Object>> getMaps(List<Blog> blogs){
        List<Map<String,Object>> maps = new ArrayList<>();

        for (Blog blog:blogs){
            HashMap<String, Object> map = new HashMap<>();
            map.put("blog",blog);
            User user = userService.findUserById(blog.getUserId());
            map.put("username",user.getUsername());
            map.put("headerUrl",user.getHeaderUrl());

            //点赞
            map.put("likeCount",likeService.findEntityLikeCount(ConstantUtil.ENTITY_TYPE_BLOG,blog.getId()));
            //浏览量
            long viewCount = viewCountService.getViewCount(ConstantUtil.ENTITY_TYPE_BLOG,blog.getId());
            map.put("viewCount",viewCount);
            maps.add(map);
        }
        return maps;
    }


}
