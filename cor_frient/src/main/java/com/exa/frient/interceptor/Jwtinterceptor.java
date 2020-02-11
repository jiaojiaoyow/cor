package com.exa.frient.interceptor;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@Component
public class Jwtinterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("拦截");
        //无论如何都放行，具体能不能操作在具体操作
        //拦截器只负责把请求头的token进行解析
        String header= request.getHeader("Authorization");
        if(header!=null||!header.isEmpty()){
            //如果包含头信息，就对其进行解析
            if (header.startsWith("Bearer")){
                //得到令牌
                String token=header.substring(7);
                //对令牌进行验证
                try {
                    Claims claims=jwtUtil.parseJWT(token);
                    String role= (String) claims.get("roles");
                    //根据权限不同在request加不同的信息
                    if(role!=null||role.equals("admin")){
                        request.setAttribute("claims_admin",claims);
                    }
                    if(role!=null||role.equals("user")){
                        request.setAttribute("claims_user",claims);
                    }
                }catch (Exception e){
                    throw new RuntimeException("token不正确");
                }
            }

        }

        return true;
    }
}
