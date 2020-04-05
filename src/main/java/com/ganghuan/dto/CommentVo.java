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
    //用于楼中楼
    private String targetName;
}
