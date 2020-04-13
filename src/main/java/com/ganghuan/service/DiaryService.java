package com.ganghuan.service;

import com.ganghuan.mapper.DiaryMapper;
import com.ganghuan.pojo.Diary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    @Autowired
    private DiaryMapper diaryMapper;

    // userid 为 0 时查询所有用户，offset,limit，分页
    public List<Diary> findDiarys(int userId, int offset, int limit){
        return diaryMapper.selectDiarys(userId,offset,limit);
    }

    public int findDiaryRows(int userId){
        return diaryMapper.selectDiaryRows(userId);
    }

    public int addDiary(Diary diary){
        if(diary == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        return diaryMapper.insertDiary(diary);
    }

}
