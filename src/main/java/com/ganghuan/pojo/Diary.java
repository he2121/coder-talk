package com.ganghuan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    private int id;
    private int userId;
    private String content;
    private String imageUrl;
    private int status;
    private Date createTime;
}
