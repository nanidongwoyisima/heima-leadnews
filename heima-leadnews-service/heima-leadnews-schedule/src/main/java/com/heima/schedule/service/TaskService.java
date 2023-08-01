package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @author 嗯呐
 * @version 1.0.0
 * @date 2023/07/28
 */
public interface TaskService {

    /**
     * 添加任务
     *
     * @param task 任务
     * @return long
     */
    long addTask(Task task);

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return boolean
     */
    boolean cancelTask(Long taskId);

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type     类型
     * @param priority 优先级
     * @return {@link Task}
     */
    Task poll(int type,int priority);
}
