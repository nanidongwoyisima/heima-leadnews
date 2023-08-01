package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 列表文章查询                                                      
      * @Description
     * @author enna
     * @date 2023/7/25 17:30
     * @param dto
     * @return ResponseResult
     */

     ResponseResult findAll(WmNewsPageReqDto dto);
    /**
     *  发布文章或保存草稿
     * @param dto
     * @return
     */
     ResponseResult submitNews(WmNewsDto dto);


    /**
     * 下架或上架
     *
     * @param dto dto
     * @return {@link ResponseResult}
     */
    ResponseResult downOrUp(WmNewsDto dto);

}
