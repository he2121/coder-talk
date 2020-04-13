package com.ganghuan.service;


import com.ganghuan.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import org.springframework.stereotype.Service;

@Service
public class LikeService {


    @Autowired
    private RedisTemplate redisTemplate;

    public void like(int userId, int entityType, int entityId,int entityUserId){


        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisUtil.getUserLikeKey(entityUserId);
                Boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);
                redisOperations.multi();
                if (isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey,1L);
                }else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                redisOperations.exec();
                return null;
            }
        });

    }

    //查询点赞数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询点赞一个否
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0;
    }

    //用户赞总数
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisUtil.getUserLikeKey(userId);

        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
