package com.asule.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.asule.common.Const;
import com.asule.common.ResponseCode;
import com.asule.common.ServerResponse;
import com.asule.entity.User;
import com.asule.service.IOrderService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * created by asule on 2020-04-21 22:56
 */
@RequestMapping("/order")
@Controller
public class OrderController {

    private static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /*调用预下单请求API生成二维码*/
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }


    /*用户支付时，支付宝会将该笔订单的变更信息，通过POST请求访问用户定义的回调路径，支付宝会回调该路径*/
    /*仅收到过支付成功后的支付宝回调*/
    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }

        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        /*
            还需要验证回调是否为支付宝所请求的。
            根据支付宝文档说明，需要对除掉sign、sign_type的所有返回参数进行验证。

            在AlipaySignature.rsaCheckV1方法中，完成了验证的绝大多数步骤。
            比如对sign使用 base64 解码，
            使用RSA的验签方法，对签名字符串、签名参数（经过 base64 解码）及支付宝公钥验证签名。

            但当前使用的RSA2的签名类型。所以在这里另一个验证签名的方法，使用rsaCheckV2方法，它可以传入签名类型。
         */

        /*rsaCheckV2方法中仅删除sign参数*/
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),
                    "utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createError("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }

        //todo 验证各种数据

        ServerResponse serverResponse = iOrderService.alicallback(params);
        //返回支付"success"或"error"
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;



//        ServerResponse serverResponse = iOrderService.aliCallback(params);
//        if(serverResponse.isSuccess()){
//            return Const.AlipayCallback.RESPONSE_SUCCESS;
//        }
//        return Const.AlipayCallback.RESPONSE_FAILED;
    }

}
