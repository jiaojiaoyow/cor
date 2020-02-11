package com.exa.web.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class WebFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 前端网关只需要转发头信息
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //得到request上下文
        RequestContext requestContext=RequestContext.getCurrentContext();

        //得到request域
        HttpServletRequest request=requestContext.getRequest();

        //得到头信息
        String header=request.getHeader("Authorization");


        //判断是否有头信息
        // 判断是否有头信息
        if (!StringUtils.isEmpty(header)) {
            // 把头信息继续往下传
            requestContext.addZuulRequestHeader("Authorization", header);
        }
        return null;
    }
}
