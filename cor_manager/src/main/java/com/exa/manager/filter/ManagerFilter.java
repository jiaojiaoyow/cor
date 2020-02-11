package com.exa.manager.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

public class ManagerFilter extends ZuulFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 当前过滤器的类型
     * @return
     */
    @Override
    public String filterType() {
        //代表当前过滤器是执行前过滤
        return "pre";
    }

    /**
     * 多个过滤器的执行顺序，数字越小越先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 当前过滤器是否开启
     */
    @Override
    public boolean shouldFilter() {
        return false;
    }

    /**
     * 过滤器内执行的操作，
     * return任意object值都为继续执行
     * setsendzuulRespponse(false)表示不再执行
     *
     *
     * 后台网关需要先验证头信息
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 获取request域
        HttpServletRequest request = currentContext.getRequest();


        /**
         *  对于网关内部执行的options方法，不需要拦截
         *  options方法（
         *  判断当前方法是到哪一个服务去的
         *  ）
         */
        if (request.getMethod().equals("OPTIONS")) {
            return null;
        }
        // 登录不需要判断
        if (request.getRequestURL().toString().indexOf("login") > 0) {
            return null;
        }

        // 得到头信息
        String header = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(header)) {
            if (header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {

                    Claims claims = jwtUtil.parseJWT(token);
                    String role = (String) claims.get("roles");
                    if (role.equals("admin")) {
                        // 把头信息转发下去， 并且放行
                        currentContext.addZuulRequestHeader("Authorization", header);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    currentContext.setSendZuulResponse(false);  // 终止运行
                }
            }
        }
        currentContext.setSendZuulResponse(false);      // 终止运行
        currentContext.setResponseStatusCode(403);      // 权限不足
        currentContext.setResponseBody("权限不足");
        currentContext.getResponse().setContentType("text/html;charset=utf-8");
        return null;
    }
}
