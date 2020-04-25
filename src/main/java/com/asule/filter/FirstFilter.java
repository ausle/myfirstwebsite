package com.asule.filter;

import com.asule.common.Const;
import com.asule.entity.User;
import com.asule.utils.CookUtils;
import com.asule.utils.JsonUtil;
import com.asule.utils.RedisPoolUtil;
import com.asule.utils.ShardRedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * created by asule on 2020-04-25 08:54
 */
public class FirstFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        /*
            因为保存在redis中的用户信息的有效期是30分钟，那30分钟后，就不得不需要重新登录。
            为了解决这个问题，需要在每次访问时重置，redis中的用户信息的有效期。

            前提条件是该cookie存在，redis中有该用户信息。当用户登出或cookie过期时则不会再重置时长。
        */

        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;

        String loginToken = CookUtils.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            User user=JsonUtil.string2Obj(ShardRedisPoolUtil.get(loginToken), User.class);
            if (user!=null){
                ShardRedisPoolUtil.expire(loginToken, Const.RedisExpieTime.expireNormalTime);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
