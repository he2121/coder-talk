package com.ganghuan.controller;

import com.ganghuan.dto.LoginHold;
import com.ganghuan.dto.Page;
import com.ganghuan.pojo.Diary;
import com.ganghuan.pojo.User;
import com.ganghuan.service.DiaryService;
import com.ganghuan.util.ConstantUtil;
import com.ganghuan.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.network.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Controller
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Value("${UPLOAD_PATH}")
    private String UPLOAD_PATH;

    @Value("${DOMAIN}")
    private String DOMAIN;

    @Autowired
    private LoginHold loginHold;
    @RequestMapping("/diary/{userId}")
    public String diary(@PathVariable("userId") int userId, Model model, Page page){

        page.setRows(diaryService.findDiaryRows(userId));
        String path = "/diary/"+userId;
        page.setPath(path);


        List<Diary> diarys = diaryService.findDiarys(userId, page.getOffset(), page.getLimit());
        model.addAttribute("diarys",diarys);
        return "timeline";
    }

    @PostMapping("/diary/add")
    public String addDiary(String content, MultipartFile image, Model model){
        User user = loginHold.getUser();
        Diary diary = new Diary();
        diary.setContent(content);
        diary.setCreateTime(new Date());
        diary.setUserId(user.getId());

        String fileName = upload(image);
        if (StringUtils.isNotBlank(fileName)){
            String imageUrl = DOMAIN + "/user/header/" + fileName;
            diary.setImageUrl(imageUrl);
        }

        diaryService.addDiary(diary);
        return "redirect:/diary/"+ user.getId();
    }

    private String upload(MultipartFile image){
        // 上传文件
        if (image == null) {
            throw new RuntimeException("图片为空");
        }

        String fileName = image.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) return null;

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            throw new RuntimeException("图片格式不正确");
        }

        // 生成随机文件名
        fileName = RandomUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(UPLOAD_PATH + "/" + fileName);
        try {
            // 存储文件
            image.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        return fileName;
    }
}
