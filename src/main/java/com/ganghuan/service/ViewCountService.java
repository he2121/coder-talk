package com.ganghuan.service;

import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViewCountService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void incrementViewCount(int entityType, int entityId){
        String entityViewKey = ConstantUtil.PREFIX_ENTITY_View+ConstantUtil.SPLIT+entityType+ConstantUtil.SPLIT+entityId;
        redisTemplate.opsForValue().increment(entityViewKey,1L);
        // 热度
        redisTemplate.opsForZSet().incrementScore(ConstantUtil.PREFIX_HOTBLOG, String.valueOf(entityId),1);
    }

    public long getViewCount(int entityType, int entityId){
        String entityViewKey = ConstantUtil.PREFIX_ENTITY_View+ConstantUtil.SPLIT+entityType+ConstantUtil.SPLIT+entityId;

        return redisTemplate.opsForValue().get(entityViewKey) == null? 0: Long.parseLong(redisTemplate.opsForValue().get(entityViewKey));
    }
}
