package com.ganghuan.mapper;

import com.ganghuan.pojo.Diary;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiaryMapper {
    List<Diary> selectDiarys(int userId, int offset, int limit);

    int selectDiaryRows(@Param("userId") int userId);

    int insertDiary(Diary diary);
}
