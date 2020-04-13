package com.ganghuan.dto;

import com.ganghuan.pojo.Comment;
import com.ganghuan.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CommentVo {

    private Comment comment;
    private User user;
    private List<CommentVo> replyVoList;
    //用于楼中楼 评论无
    private String targetName;

    // 点赞状态 回复无
    private long likeCount;
    private int likeStatus;
}
