package com.ganghuan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int status;
    private Date createTime;
    private Date updateTime;
    private int commentCount;
    private int viewCount;
    private double score;
}
