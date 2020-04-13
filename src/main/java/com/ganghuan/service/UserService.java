package com.ganghuan.service;

import com.ganghuan.mapper.LoginTicketMapper;
import com.ganghuan.mapper.UserMapper;
import com.ganghuan.pojo.LoginTicket;
import com.ganghuan.pojo.User;
import com.ganghuan.util.RandomUtil;
import com.ganghuan.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public User findUserById(int id){
        //return userMapper.selectById(id);
        User user = getCacheId(id);
        if (user == null) user = initCache(id);
        return user;
    }

    private User getCacheId(int userId){
        String userKey = RedisUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    private void clearCache(int userId){
        String userKey = RedisUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    public User findUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }



    public Map<String, Object> register(User user,String code,String trueCode) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        if (StringUtils.isBlank(code)) {
            map.put("codeMsg", "验证码不能为空!");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 验证邮箱注册验证码
        if(!StringUtils.equals(code,trueCode)){
            map.put("codeMsg", "验证码错误!");
            return map;
        }

        // 注册用户

        user.setPassword(RandomUtil.md5(user.getPassword()));
        user.setType(0);
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        return map;
    }


    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证密码
        password = RandomUtil.md5(password);
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(RandomUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        //loginTicketMapper.updateStatus(ticket,1);
        String ticketKey = RedisUtil.getTicketKey(ticket);
        LoginTicket o = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        o.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,o);

    }

    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.selectByTicket(ticket);
        String ticketKey = RedisUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }
}


