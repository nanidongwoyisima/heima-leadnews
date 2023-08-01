package com.heima.article.service.Impl;

import com.heima.article.service.ApUserSearchService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.pojos.ApUserSearch;
import com.heima.model.user.pojos.ApUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/08/01/10:45
 * @Description:
 */
@Service
@Slf4j
public class ApUserSearchServiceImpl implements ApUserSearchService {
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 保存用户搜索历史记录
     *
     * @param keyword
     * @param userId
     */
    @Override
    @Async
    public void insert(String keyword, Integer userId) {
        //1.查询当前用户的搜索关键词
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
        ApUserSearch apUserSearch=mongoTemplate.findOne(query,ApUserSearch.class);
        //2.存在 更新创建时间
        if (apUserSearch!=null){
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
        }
        //3.不存在，判断当前历史记录总数量是否超过10
        apUserSearch=new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());

        Query query1=Query.query(Criteria.where("userId").is(userId));

        query1.with(Sort.by(Sort.Direction.DESC,"createdTime"));

       List<ApUserSearch> apUserSearchList=mongoTemplate.find(query1,ApUserSearch.class);

        if (apUserSearchList==null&&apUserSearchList.size()<10) {
            mongoTemplate.save(apUserSearchList);
        }else {
            ApUserSearch lastUserSearch=apUserSearchList.get(apUserSearchList.size()-1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())),apUserSearch);
        }

    }

    /**
     * 查询搜索历史
     *
     * @return
     */
    //@Override
//    public ResponseResult findUserSearch() {//获取当前用户
//    ApUser user = AppThreadLocalUtil.getUser();
//    if(user == null){
//        return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
//    }
//    //根据用户查询数据，按照时间倒序
//    List<ApUserSearch> apUserSearches = mongoTemplate.find(
//            Query.query(Criteria.where("userId").is(user.getId()))
//                    .with(Sort.by(Sort.Direction.DESC, "createdTime"))
//                                , ApUserSearch.class);
//    return ResponseResult.okResult(apUserSearches);
//    }
}