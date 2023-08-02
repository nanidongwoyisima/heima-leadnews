package com.heima.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/08/02/16:20
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.admin.mapper")
public class AdminMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminMainApplication.class,args);
    }
}
