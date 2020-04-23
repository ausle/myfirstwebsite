package com.asule.service.impl;

import com.asule.common.Const;
import com.asule.common.ResponseCode;
import com.asule.common.ServerResponse;
import com.asule.dao.CartMapper;
import com.asule.dao.ProductMapper;
import com.asule.entity.Cart;
import com.asule.entity.Product;
import com.asule.service.ICartService;
import com.asule.utils.BigDecimalUtil;
import com.asule.utils.PropertiesUtil;
import com.asule.vo.CartProductVo;
import com.asule.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * created by asule on 2020-04-21 14:58
 */
@Service
public class CartServiceImpl implements ICartService{


    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /*
        一个购物车条目对应一个商品。
    */
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServerResponse.createError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);

        if(cart == null){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServerResponse.createError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return this.list(userId);
    }


    /*多个商品id用逗号连接*/
    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createSuccessByData(cartVo);
    }


    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        /*该用户的所有购物车条目*/
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (cartList!=null&&cartList.size()>0){
            for(Cart cartItem : cartList){

                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());

                if(product != null){
                    //为cartProductVo添加商品相关信息
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        /*若大于库存，则更新购物车中该商品的库存为可购买的最大库存*/
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    cartProductVo.setQuantity(buyLimitCount);

                    /*计算每个商品价格*/
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());

                    if(cartItem.getChecked() == Const.Cart.CHECKED){
                        //如果已经勾选,增加到整个的购物车总价中
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }
            }

            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setAllChecked(this.getAllCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

            return cartVo;
        }
        return null;
    }

    /*购物车的购物条目是否都勾选*/
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;

    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if(userId == null){
            return ServerResponse.createSuccessByData(0);
        }
        return ServerResponse.createSuccessByData(cartMapper.selectCartProductCount(userId));
    }
}
