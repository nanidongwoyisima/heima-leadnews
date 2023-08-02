package com.heima.admin.controller.v1;

import com.heima.admin.service.AdService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/08/02/16:33
 * @Description:
 */
@RestController
@RequestMapping("/login")
public class AdUserLoginController {

    @Autowired
    private AdService adService;


    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdUserDto dto){
        return adService.login(dto);
    }

}
