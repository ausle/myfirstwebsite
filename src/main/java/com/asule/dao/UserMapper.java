package com.asule.dao;

import com.asule.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(@Param(value = "username")String name);

    int checkEmail(@Param(value = "email")String email);

    User loginUser(@Param(value = "username") String username,@Param(value = "password")String password);

    int checkEmailRepeat(@Param(value = "email") String email,@Param(value = "userId")Integer userId);

    String selectQuestion(@Param("username")String username);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    int updatePasswordByUsername(@Param("username")String username,@Param("password")String password);

    int checkPassword(@Param("password")String username,@Param("userId")Integer userId);

    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}