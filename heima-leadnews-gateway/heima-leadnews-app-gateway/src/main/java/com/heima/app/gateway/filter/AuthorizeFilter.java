package com.heima.app.gateway.filter;

import com.heima.app.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/07/06/9:14
 * @Description:
 */
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.判断是否登录
        if (request.getURI().getPath().contains("/login")) {
            return chain.filter(exchange);
        }
        //3.获取token
        String token = request.getHeaders().getFirst("token");

        //4.判断token是否存在
        if (StringUtils.isBlank(token)) {
            //不存在返回401状态码  UNAUTHORIZED(401, "Unauthorized"),
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //5。判断token是否过期
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result==1||result==2) {
                //过期 返回401状态码  UNAUTHORIZED(401, "Unauthorized"),
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            //获取token中的用户信息
            Object userId = claimsBody.get("id");
            //存入header
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();

            exchange.mutate().request(serverHttpRequest).build();
        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //6.放行
        return chain.filter(exchange);
    }



    /**
     *
      * @Description
     * @author enna
     * @date 2023/7/6 9:15
     * @param //优先级设置  值越小，优先级越高

     */
    @Override
    public int getOrder() {
        return 0;
    }
}
