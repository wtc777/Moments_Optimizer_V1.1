package com.moments.optimizer.mapper;

import com.moments.optimizer.domain.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {

    void insertTask(Task task);

    Task selectById(@Param("id") String id);

    List<Task> selectRunnableTasks(@Param("limit") int limit);

    int updateStatus(@Param("id") String id,
                     @Param("status") String status,
                     @Param("errorMessage") String errorMessage,
                     @Param("updatedAt") LocalDateTime updatedAt,
                     @Param("resultJson") String resultJson);
}
