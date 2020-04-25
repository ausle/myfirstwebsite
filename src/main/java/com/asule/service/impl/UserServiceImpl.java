package com.asule.service.impl;

import com.asule.common.Const;
import com.asule.common.ServerResponse;
import com.asule.common.TokenCache;
import com.asule.dao.UserMapper;
import com.asule.entity.User;
import com.asule.service.IUserService;
import com.asule.utils.MD5Util;
import com.asule.utils.RedisPoolUtil;
import com.asule.utils.ShardRedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * created by asule on 2020-01-18 13:14
 */
@Service
public class UserServiceImpl implements IUserService{

    public static final String TOKEN_PREFIX = "token_";

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0 ){
            return ServerResponse.createError("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user  = userMapper.loginUser(username,md5Password);
        if(user == null){
            return ServerResponse.createError("密码错误");
        }
        /*置为空*/
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createSuccessByData("登录成功",user);
    }

    @Override
    public ServerResponse register(User user) {
        ServerResponse<String> responseName = checkValid(user.getUsername(), Const.USERNAME);
        if (!responseName.isSuccess()){
            return responseName;
        }
        ServerResponse<String> responseEmail = checkValid(user.getEmail(), Const.EMAIL);
        if (!responseEmail.isSuccess()){
            return responseEmail;
        }
        /*默认角色*/
        user.setRole(Const.Role.NORMAL_USER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int insertCount = userMapper.insert(user);
        if (insertCount==0){
            return ServerResponse.createError("注册失败");
        }
        return ServerResponse.createSuccess("注册成功");
    }


    @Override
    public ServerResponse<String> checkValid(String str, String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0 ){
                    return ServerResponse.createError("用户已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0 ){
                    return ServerResponse.createError("email已存在");
                }
            }
        }else{
            return ServerResponse.createError("参数错误");
        }
        return ServerResponse.createSuccess("校验成功");
    }

    @Override
    public ServerResponse selectQuestion(String username) {
        ServerResponse<String> responseName = checkValid(username, Const.USERNAME);
        if (responseName.isSuccess()){
            return ServerResponse.createError("用户不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createSuccess(question);
        }
        return ServerResponse.createError("用户问题不存在");
    }


    /**/
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        ServerResponse<String> responseName = checkValid(username, Const.USERNAME);
        if (responseName.isSuccess()){
            return ServerResponse.createError("用户不存在");
        }

        int checkAnswer = userMapper.checkAnswer(username, question, answer);
        if (checkAnswer>0){
            //说明问题及问题答案是这个用户的,并且是正确的。添加token到redis缓存(guvva缓存)
            String forgetToken = UUID.randomUUID().toString();
            ShardRedisPoolUtil.setEx(TOKEN_PREFIX+username,forgetToken,30*60*12);
//            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createSuccess(forgetToken);
        }
        return ServerResponse.createError("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createError("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createError("用户不存在");
        }
        String token =ShardRedisPoolUtil.get(TOKEN_PREFIX+username);
//        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createError("token无效或者过期");
        }
        /**/
        if(StringUtils.equals(forgetToken,token)){
            String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount > 0){
                return ServerResponse.createSuccess("修改密码成功");
            }
        }else{
            return ServerResponse.createError("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createError("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createError("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createSuccess("密码更新成功");
        }
        return ServerResponse.createError("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createError("email已存在,请更换email再尝试更新");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createSuccessByData("更新个人信息成功",updateUser);
        }
        return ServerResponse.createError("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createSuccess("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createSuccessByData("用户信息",user);

    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createSuccess();
        }
        return ServerResponse.createError();
    }
}
