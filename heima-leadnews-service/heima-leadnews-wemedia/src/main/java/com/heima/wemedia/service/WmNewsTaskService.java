package com.heima.wemedia.service;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/07/28/15:24
 * @Description:
 */
public interface WmNewsTaskService {


    /**
     * 添加任务到延迟队列中
     * @param id  文章的id
     * @param publishTime  发布的时间  可以做为任务的执行时间
     */
    void addNewsTask(Integer id, Date publishTime);
    /**
     * 消费延迟队列数据
     */
    void scanNewsByTask();

}
