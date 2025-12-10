package com.moments.optimizer.mapper;

import com.moments.optimizer.domain.AnalysisHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnalysisHistoryMapper {

    List<AnalysisHistory> selectPageByUser(@Param("userId") String userId,
                                           @Param("offset") int offset,
                                           @Param("size") int size);

    long countByUser(@Param("userId") String userId);

    AnalysisHistory selectById(@Param("id") String id);
}
