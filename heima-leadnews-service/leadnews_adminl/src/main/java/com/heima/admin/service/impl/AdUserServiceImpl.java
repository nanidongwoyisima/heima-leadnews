package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.AppJwtUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/08/02/16:31
 * @Description:
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdService {
    @Override
    public ResponseResult login(AdUserDto dto) {
        //1.正常登录
        if (StringUtils.isNotBlank(dto.getName()) && StringUtils.isNotBlank(dto.getPassword())) {
            //1.1 根据手机号查询用户信息
            AdUser adUser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
            if (adUser==null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }
            //1.2 比对密码
            String salt = adUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswd.equals(adUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //1.3 返回数据 jwt user
            String token = AppJwtUtil.getToken(adUser.getId().longValue());
            Map<String,Object> map=new HashMap<>();
            map.put("token",token);
            adUser.setSalt("");
            adUser.setPassword("");
            map.put("user",adUser);
            return  ResponseResult.okResult(map);
        }else {
            //2.游客登录
            Map<String,Object> map=new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0L));
            return  ResponseResult.okResult(map);
        }
    }
}
