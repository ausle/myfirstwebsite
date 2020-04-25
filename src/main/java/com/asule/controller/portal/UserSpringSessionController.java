package com.asule.controller.portal;


import com.asule.common.Const;
import com.asule.common.ServerResponse;
import com.asule.entity.User;
import com.asule.service.IUserService;
import com.asule.utils.CookUtils;
import com.asule.utils.JsonUtil;
import com.asule.utils.ShardRedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * created by asule on 2020-01-18 13:17
 */

/*测试spring-session的功能*/
@Controller
@RequestMapping("/user/spring_session")
public class UserSpringSessionController {

    @Autowired
    public IUserService userService;

    @ResponseBody
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    public ServerResponse login(HttpSession session, String username, String password){
        ServerResponse<User> login = userService.login(username, password);
        if (login.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,login.getData());
        }
        return login;
    }


    /**/
    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){

        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createError("用户未登录,无法获取当前用户的信息");
        }

        return ServerResponse.createSuccessByData(user);

    }

    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession session){

        session.removeAttribute(Const.CURRENT_USER);

        return ServerResponse.createSuccess("登出成功");
    }
}
