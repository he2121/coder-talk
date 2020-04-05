package com.ganghuan.service;

import com.ganghuan.mapper.BlogMapper;
import com.ganghuan.pojo.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogMapper blogMapper;

    // userid 为 0 时查询所有用户，offset,limit，分页
    public List<Blog> findBlogs(int userId, int offset, int limit){
        return blogMapper.selectBlogs(userId,offset,limit);
    }

    public int findBlogsRows(int userId){
        return blogMapper.selectBlogRows(userId);
    }

    public int addBlog(Blog blog){
        if(blog == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        return blogMapper.insertBlog(blog);
    }

    public Blog findBlogById(int blogId) {
        return blogMapper.selectBlogById(blogId);
    }
}
