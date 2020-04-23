package com.asule.service;

import com.asule.common.ServerResponse;
import com.asule.entity.Product;
import com.github.pagehelper.PageInfo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);
}
