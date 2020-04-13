package com.ganghuan.controller;

import com.ganghuan.dto.LoginHold;
import com.ganghuan.event.EventProducer;
import com.ganghuan.pojo.Event;
import com.ganghuan.pojo.User;
import com.ganghuan.service.LikeService;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.JsonUtil;
import com.ganghuan.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LoginHold loginHold;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @PostMapping("/like")
    @ResponseBody
    public String like(Integer entityType, Integer entityId,int entityUserId, int blogId){

        User user = loginHold.getUser();
        if (user == null) throw new RuntimeException("用户没有登录！");
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String,Object> map = new HashMap<>();

        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //消息队列
        if(likeStatus == 1){
            Event event = new Event().setTopic(ConstantUtil.TOPIC_LIKE).setUserId(loginHold.getUser().getId()).setEntityType(entityType).setEntityId(entityId).setEntityUserId(entityUserId)
                    .setData("blogId",blogId);
            eventProducer.fireEvent(event);

            //热度
            redisTemplate.opsForZSet().incrementScore(ConstantUtil.PREFIX_HOTBLOG, String.valueOf(blogId),3);
        }


        return JsonUtil.getJSONString(0,null,map);
    }

}
