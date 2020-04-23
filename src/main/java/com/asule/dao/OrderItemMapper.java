package com.asule.dao;

import com.asule.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);


    void batchInsert(@Param("orderItems") List<OrderItem> orderItems);


    List<OrderItem> getByOrderNoUserId(@Param("orderNo")Long orderNo,@Param("userId")Integer userId);

}