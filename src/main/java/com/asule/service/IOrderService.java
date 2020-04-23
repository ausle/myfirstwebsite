package com.asule.service;

import com.asule.common.ServerResponse;
import com.asule.vo.OrderVo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * created by asule on 2020-04-21 23:14
 */
public interface IOrderService {

    /*根据订单去支付*/
    ServerResponse pay(Long orderNo,Integer userId,String path);

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    /*验证是支付宝回调后，根据支付宝返还的参数，进行开发*/
    ServerResponse alicallback(Map<String,String> params);
}
