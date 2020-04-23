package com.asule.controller.portal;


import com.asule.common.Const;
import com.asule.common.ResponseCode;
import com.asule.common.ServerResponse;
import com.asule.entity.TestData;
import com.asule.entity.User;
import com.asule.service.IUserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * created by asule on 2020-01-18 13:17
 */
@Controller
@RequestMapping("/user")
public class UserController{

    @Autowired
    public IUserService userService;

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ServerResponse login(HttpSession session,String username, String password){
        ServerResponse<User> login = userService.login(username, password);
        if (login.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,login.getData());
        }
        return login;
    }


    /*
        对于POST请求，无论我是否设置encoding的过滤器。
        都起不到作用，只有修改tomcat的URIEncoding为utf-8才不乱码。
        那么我只能认为，在进行参数绑定时，已经默认使用了tomcat使用的"iso-8859-1"码表。
        在filter进行的设置无效。
     */
    @ResponseBody
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ServerResponse register(User user) throws UnsupportedEncodingException {
        return userService.register(user);
    }


    @RequestMapping(value = "/get_question",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return userService.selectQuestion(username);
    }

    @RequestMapping(value = "/check_answer",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "/forget_reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return userService.forgetResetPassword(username,passwordNew,forgetToken);
    }


    @RequestMapping(value = "/reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createError("用户未登录");
        }
        return userService.resetPassword(passwordOld,passwordNew,user);
    }


    @RequestMapping(value = "/update_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createError("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = userService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**/
    @RequestMapping(value = "/get_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createError(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return userService.getInformation(currentUser.getId());
    }

    @RequestMapping(value = "/get_userinfo",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createSuccessByData(user);
        }
        return ServerResponse.createError("用户未登录,无法获取当前用户的信息");
    }
}
