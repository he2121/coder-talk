package com.ganghuan.dto;

import com.ganghuan.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class LoginHold {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
