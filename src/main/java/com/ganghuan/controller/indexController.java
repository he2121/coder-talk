package com.ganghuan.controller;

import com.ganghuan.dto.Page;
import com.ganghuan.pojo.Blog;
import com.ganghuan.pojo.User;
import com.ganghuan.service.BlogService;
import com.ganghuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class indexController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private UserService userService;

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

        List<Map<String,Object>> maps = new ArrayList<>();

        for (Blog blog:blogs){
            HashMap<String, Object> map = new HashMap<>();
            map.put("blog",blog);
            User user = userService.findUserById(blog.getUserId());
            map.put("username",user.getUsername());
            map.put("headerUrl",user.getHeaderUrl());
            maps.add(map);
        }
        model.addAttribute("maps",maps);
        return "index";
    }
}
