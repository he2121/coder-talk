package com.ganghuan.controller;

import com.ganghuan.dto.LoginHold;
import com.ganghuan.dto.Page;
import com.ganghuan.event.EventProducer;
import com.ganghuan.pojo.Event;
import com.ganghuan.pojo.User;
import com.ganghuan.service.FollowService;
import com.ganghuan.service.UserService;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static com.ganghuan.util.ConstantUtil.ENTITY_TYPE_USER;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private LoginHold loginHold;

    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType,int entityId){
        followService.follow(loginHold.getUser().getId(),entityType,entityId);

        // 消息队列
        Event event = new Event().setTopic(ConstantUtil.TOPIC_FOLLOW).setUserId(loginHold.getUser().getId())
                .setEntityType(entityType).setEntityId(entityId).setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return JsonUtil.getJSONString(0,"关注成功");
    }
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        followService.unfollow(loginHold.getUser().getId(),entityType,entityId);
        return JsonUtil.getJSONString(0,"取消关注成功");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "follow";
    }


    @GetMapping(path = "/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "follow";
    }


    private boolean hasFollowed(int userId) {
        if (loginHold.getUser() == null) {
            return false;
        }

        return followService.hasFollowed(loginHold.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

}
