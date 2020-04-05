package com.ganghuan.mapper;

import com.ganghuan.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
