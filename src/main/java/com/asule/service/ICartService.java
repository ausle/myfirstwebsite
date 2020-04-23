package com.asule.service;


import com.asule.common.ServerResponse;
import com.asule.vo.CartVo;

/**
 * Created by geely
 */
public interface ICartService {

    /*添加商品到购物车*/
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    /*更新商品数目*/
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);


    /*删除购物车商品*/
    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    /*获取购物车明细*/
    ServerResponse<CartVo> list(Integer userId);

    /**/
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
