package com.asule.service;

import com.asule.common.ServerResponse;
import com.asule.entity.User;

/**
 * created by asule on 2020-01-18 13:14
 */
public interface IUserService {

    ServerResponse login(String username,String password);

    ServerResponse register(User user);

    /*检查name、email是否存在*/
    ServerResponse checkValid(String checkValue,String checkType);

    /*查询问题*/
    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    /*重置密码*/
    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    /*直接修改密码*/
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    /*检查是否是管理员*/
    ServerResponse checkAdminRole(User user);
}
