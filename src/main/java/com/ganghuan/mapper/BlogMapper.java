package com.ganghuan.mapper;

import com.ganghuan.pojo.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BlogMapper {

    //分页查询博客列表
    List<Blog> selectBlogs(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectBlogRows(@Param("userId") int userId);

    int insertBlog(Blog blog);

    Blog selectBlogById(int blogId);

    int updateCommentCount(int id, int commentCount);
}
