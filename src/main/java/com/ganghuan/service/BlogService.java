package com.ganghuan.service;

import com.ganghuan.mapper.BlogMapper;
import com.ganghuan.pojo.Blog;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private RedisTemplate redisTemplate;

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

    public List<Blog> findCircleBlogs(int userId, int offset, int limit){
        String followeeKey = RedisUtil.getFolloweeKey(userId, ConstantUtil.ENTITY_TYPE_USER);
        Set<Integer> userIds = redisTemplate.opsForZSet().range(followeeKey,0,Integer.MAX_VALUE);
        if (userIds == null ||userIds.isEmpty() ) return new ArrayList<>();
        return blogMapper.selectCircleBlogs(new ArrayList<Integer>(userIds),offset,limit);
    }

    public int findCircleBlogsRows(int userId){
        String followeeKey = RedisUtil.getFolloweeKey(userId, ConstantUtil.ENTITY_TYPE_USER);
        Set<Integer> userIds = redisTemplate.opsForZSet().range(followeeKey,0,Integer.MAX_VALUE);
        if (userIds == null || userIds.isEmpty()) return 0;
        return blogMapper.selectCircleBlogRows(new ArrayList<Integer>(userIds));
    }
}
