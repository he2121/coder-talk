package com.ganghuan.controller;


import com.ganghuan.dto.CommentVo;
import com.ganghuan.dto.LoginHold;
import com.ganghuan.dto.Page;
import com.ganghuan.pojo.Comment;
import com.ganghuan.pojo.User;
import com.ganghuan.service.CommentService;
import com.ganghuan.service.FollowService;
import com.ganghuan.service.LikeService;
import com.ganghuan.service.UserService;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LoginHold loginHold;

    @Autowired
    private FollowService followService;

    @Value("${UPLOAD_PATH}")
    private String UPLOAD_PATH;

    @Value("${DOMAIN}")
    private String DOMAIN;

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "setting";
    }


    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "setting";
        }

        // 生成随机文件名
        fileName = RandomUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(UPLOAD_PATH + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/user/header/xxx.png
        User user = loginHold.getUser();
        String headerUrl = DOMAIN + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = UPLOAD_PATH + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }


    @GetMapping("/commentboard/{userId}")
    public String messageboard(@PathVariable("userId") int userId, Model model, Page page){

        // 评论分页信息
        page.setPath("/user/commentboard/" + userId);
        page.setRows(commentService.findCommentCount(ConstantUtil.ENTITY_TYPE_USER,userId));

        // 添加评论，回复
        List<Comment> comments = commentService.findCommentsByEntity(ConstantUtil.ENTITY_TYPE_USER,userId,page.getOffset(),page.getLimit());
        List<CommentVo> commentsVo = getCommentsVoFromComments(comments);
        model.addAttribute("commentsVo",commentsVo);
        model.addAttribute("userId",userId);
        return "commentboard";
    }

    public List<CommentVo> getCommentsVoFromComments(List<Comment> comments) {

        List<CommentVo> commentsVo = new ArrayList<>();
        if (comments != null){
            for (Comment comment:comments){
                CommentVo commentVo = new CommentVo();
                // 评论
                commentVo.setComment(comment);
                // 作者
                commentVo.setUser(userService.findUserById(comment.getUserId()));

                // 点赞
                commentVo.setLikeCount(likeService.findEntityLikeCount(2,comment.getId()));
                commentVo.setLikeStatus(likeService.findEntityLikeStatus(loginHold.getUser().getId(),2,comment.getId()));
                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                List<CommentVo> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply:replyList){
                        CommentVo replyVo = new CommentVo();
                        replyVo.setComment(reply);
                        replyVo.setUser(userService.findUserById(reply.getUserId()));
                        replyVo.setReplyVoList(new ArrayList<>());
                        //回复专用
                        replyVo.setTargetName(userService.findUserById(reply.getTargetId()).getUsername());
                        replyVoList.add(replyVo);
                    }
                    commentVo.setReplyVoList(replyVoList);
                    commentsVo.add(commentVo);
                }
            }
        }

        return commentsVo;
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);

        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("userLikeCount",userLikeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ConstantUtil.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);

        //粉丝数量
        long followerCount = followService.findFollowerCount(userId, ConstantUtil.ENTITY_TYPE_USER);
        model.addAttribute("followerCount",followerCount);

        //
        boolean hasFollowed = false;
        if(loginHold.getUser() != null){
            hasFollowed = followService.hasFollowed(loginHold.getUser().getId(),ConstantUtil.ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "profile";
    }

}
