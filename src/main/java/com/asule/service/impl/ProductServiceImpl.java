package com.asule.service.impl;

import com.asule.common.ServerResponse;
import com.asule.dao.ProductMapper;
import com.asule.entity.Product;
import com.asule.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by asule on 2020-04-23 16:35
 */
@Service
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount > 0){
                    return ServerResponse.createSuccess("更新产品成功");
                }
                return ServerResponse.createSuccess("更新产品失败");
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createSuccess("新增产品成功");
                }
                return ServerResponse.createSuccess("新增产品失败");
            }
        }
        return ServerResponse.createError("新增或更新产品参数不正确");
    }
}
