package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/07/28/9:14
 * @Description:
 */
@Service
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {
    /**
     * 添加任务
     *
     * @param task 任务
     * @return long
     */
    @Override
    public long addTask(Task task) {
        //1.添加任务到数据库中
        boolean succes=addTaskToDb(task);
        if (succes){
            //2.添加任务到redis
            addTaskCache(task);
        }

        return task.getTaskId();
    }

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return boolean
     */
    @Override
    public boolean cancelTask(Long taskId) {
        boolean flag=false;
        //删除任务，更新日志
        Task task=updateDb(taskId,ScheduleConstants.CANCELLED);

        //删除redis中的数据
        if (task!=null){
            removeTaskFromCache(task);
            flag=true;
        }

        return flag;
    }

    /**
     * 从缓存中删除任务
     *
     * @param task 任务
     */
    private void removeTaskFromCache(Task task) {

        String key=task.getTaskType()+"_"+task.getPriority();
        //如果时间小于等于当前,删除list中的
        if (task.getExecuteTime()<=System.currentTimeMillis()){
            cacheService.lRemove(ScheduleConstants.TOPIC+key,0, JSON.toJSONString(task));
        }else {
            cacheService.zRemove(ScheduleConstants.FUTURE+key,JSON.toJSONString(task));
        }
    }

    /**
     * 更新数据库
     *
     * @param taskId   任务id
     * @param status 状态
     * @return {@link Task}
     */
    private Task updateDb(Long taskId, int status) {
        Task task=null;
        try {
            //删除任务
            taskinfoMapper.deleteById(taskId);

            //修改日志信息
            TaskinfoLogs taskinfoLogs=taskinfoLogsMapper.selectById(taskId);
            //设置状态
            taskinfoLogs.setStatus(status);
            //修改
            taskinfoLogsMapper.updateById(taskinfoLogs);
            task=new Task();
            //对象拷贝
            BeanUtils.copyProperties(taskinfoLogs,task);
            //修改执行时间
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        }catch (Exception e){
            log.error("task cancel exception taskid={}",taskId);
        }
        return task;
    }

    @Autowired
    private CacheService cacheService;
    /**
     * 添加任务缓存
     *
     * @param task 任务
     */
    private void addTaskCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        //获取5分钟之后的时间  毫秒值
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        long nextScheduleTime = calendar.getTimeInMillis();
        //2.1 如果任务的执行时间小于等于当前时间，存入list

        if (task.getExecuteTime()<=System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC+key, JSON.toJSONString(task));

        }else if (task.getExecuteTime()<=nextScheduleTime){
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE+key,JSON.toJSONString(task),task.getExecuteTime());
        }
    }
    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    /**
     * 将任务添加到数据库
     *
     * @param task 任务
     * @return boolean
     */
    private boolean addTaskToDb(Task task) {
        boolean flag=false;
        try {
            //保存任务
            Taskinfo taskinfo=new Taskinfo();
            //内容拷贝
            BeanUtils.copyProperties(task,taskinfo);
            //给执行时间
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            //保存数据
            taskinfoMapper.insert(taskinfo);

            //设置taskId
            task.setTaskId(taskinfo.getTaskId());

            //保存任务日志数据
            TaskinfoLogs taskinfoLogs=new TaskinfoLogs();

            //对象拷贝
            BeanUtils.copyProperties(taskinfo,taskinfoLogs);
            //赋值
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);
            flag=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type     类型
     * @param priority 优先级
     * @return {@link Task}
     */
    @Override
    public Task poll(int type, int priority) {
        Task task=null;
        try {
            //设置key
            String key=type+"_"+priority;
            //设置任务json
            String task_json=cacheService.lRightPop(ScheduleConstants.TOPIC+key);
            //判断非空
            if (StringUtils.isNotBlank(task_json)){
                task=JSON.parseObject(task_json,Task.class);
            }
            //更新数据库信息
            updateDb(task.getTaskId(),ScheduleConstants.EXECUTED);
        }catch (Exception e){
            e.printStackTrace();
            log.error("pool task execption");
        }
        return task;
    }

    /**
     * 未来数据定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public  void refresh(){
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if (StringUtils.isNotBlank(token)) {

            log.error(System.currentTimeMillis()/1000+"执行了定时任务");
            // 获取所有未来数据集合的key值
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {
                String topicKey=ScheduleConstants.TOPIC+futureKey.split(ScheduleConstants.FUTURE)[1];
                //获取该组key下当前需要消费的任务数据
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                if (!tasks.isEmpty()) {
                    cacheService.refreshWithPipeline(futureKey,topicKey,tasks);
                    log.error("成功的将"+futureKey+"下的当前执行的任务数据刷新到"+topicKey+"下");
                }

            }
        }

    }

    @PostConstruct
    @Scheduled(cron = "0 */5 * * * ?")
    public  void reloadData(){
        clearCache();
        log.info("数据库同步到缓存");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        //查看小于未来为五分钟的所有任务
        List<Taskinfo> allTasks = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));

        if (allTasks!=null&&allTasks.size()>0){
            for (Taskinfo taskinfo : allTasks) {
                Task task=new Task();
                //拷贝内容
                BeanUtils.copyProperties(taskinfo,task);
                //为task设置执行时间
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                //添加缓存任务，加入到redis中
                addTaskCache(task);
            }
        }
    }

    private void clearCache() {
        // 删除缓存中未来数据集合和当前消费者队列的所有key
        Set<String> futrueKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(futrueKeys);
        cacheService.delete(topicKeys);

    }
}
