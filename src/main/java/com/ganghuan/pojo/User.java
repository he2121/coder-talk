package com.ganghuan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String email;
    private String username;
    private String password;
    // 0：普通用户 1：超级管理员
    private int type;
    private String headerUrl;
    private Date createTime;
}
