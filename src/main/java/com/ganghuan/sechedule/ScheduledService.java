package com.ganghuan.sechedule;

import com.ganghuan.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Scheduled(cron = "0 0 2 * * ? ")
    public void scheduled() {
        redisTemplate.delete(ConstantUtil.PREFIX_HOTBLOG);
        System.out.println("5 秒定时任务");
    }
}
