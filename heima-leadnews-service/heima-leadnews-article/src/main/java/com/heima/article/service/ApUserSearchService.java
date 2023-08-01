package com.heima.article.service;

import com.heima.model.common.dtos.ResponseResult;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/08/01/10:45
 * @Description:
 */
public interface ApUserSearchService {
    /**
     * 保存用户搜索历史记录
     * @param keyword
     * @param userId
     */
    public void insert(String keyword,Integer userId);
//    /**
//     查询搜索历史
//     @return
//     */
//    ResponseResult findUserSearch();

}
