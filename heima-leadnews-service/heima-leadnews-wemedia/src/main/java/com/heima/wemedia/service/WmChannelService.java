package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

public interface WmChannelService extends IService<WmChannel> {
    /** 
     * 
      * @Description 
     * @author enna
     * @date 2023/7/25 14:45
    列表查询显示
     * @return ResponseResult 
     */
    
     ResponseResult findAll();

}