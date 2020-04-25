package com.asule.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * created by asule on 2020-04-24 20:31
 */
@Slf4j
public class CookUtils {

    private static final String DOMAIN="ausle.tech";

    public static final String COOKIE_NAME="ASULE";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            if (cookie.getName().equals(COOKIE_NAME)){
                return cookie.getValue();
            }
        }
        return null;
    }


    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        /*默认的cookie是session级别cookie。存在于内存中。浏览器关闭就会清除。*/
        /*设置为-1代表永久，那么之后即使设置该cookie为0也不会生效*/
        cookie.setMaxAge(60 * 60 * 24);
//        cookie.setMaxAge(-1);//-1为永久保存在客户端
        cookie.setHttpOnly(true);//防止脚本攻击获取cookie
        cookie.setPath("/");
        //tomcat8.5之后，domain不要再以"."开头。否则会抛出An invalid domain异常信息。不加"."的效果和之前一样。
        cookie.setDomain(DOMAIN);
        response.addCookie(cookie);
    }

    public static void logoutLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies!=null&&cookies.length>0){
            for (Cookie cookie:cookies) {
                if (cookie.getName().equals(COOKIE_NAME)){
                    //不能仅仅设置cookie的maxAge为0,需要加上domain。
                    cookie.setDomain(DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    log.info("{},已失效",cookie.getName());
                    return;
                }
            }
        }
    }
}
