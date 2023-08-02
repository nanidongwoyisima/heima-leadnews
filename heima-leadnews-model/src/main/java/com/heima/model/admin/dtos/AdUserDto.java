package com.heima.model.admin.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/07/04/16:05
 * @Description:
 */

@Data
public class AdUserDto {

    /**
     * 手机号
     */
    @ApiModelProperty(value="姓名",required = true)
    private String name;

    /**
     * 密码
     */
    @ApiModelProperty(value="密码",required = true)
    private String password;
}
