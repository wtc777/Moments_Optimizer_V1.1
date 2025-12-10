package com.moments.optimizer.mapper;

import com.moments.optimizer.domain.TaskStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskStepMapper {

    void insertSteps(@Param("steps") List<TaskStep> steps);

    List<TaskStep> selectByTaskId(@Param("taskId") String taskId);

    TaskStep selectFirstPendingStep(@Param("taskId") String taskId);

    TaskStep selectById(@Param("id") Long id);

    int updateStepStatus(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("startedAt") LocalDateTime startedAt,
                         @Param("finishedAt") LocalDateTime finishedAt);
}
