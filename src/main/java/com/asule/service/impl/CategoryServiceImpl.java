package com.asule.service.impl;

import com.asule.common.ServerResponse;
import com.asule.dao.CategoryMapper;
import com.asule.entity.Category;
import com.asule.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * created by asule on 2020-04-21 13:55
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createError("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createSuccess("添加品类成功");
        }
        return ServerResponse.createError("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createError("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createSuccess("更新品类名字成功");
        }
        return ServerResponse.createError("更新品类名字失败");
    }


    /*获取当前种类的子种类*/
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createSuccessByData(categoryList);
    }



    /*获取当前种类的所有子种类*/
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categories=new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                /*id值小的靠前*/
                return o1.getId()-o2.getId();
            }
        });
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        categories.add(category);

        categories=findDeepChild(categories,categoryId);

        List categoryIds=new ArrayList<Integer>();
        Iterator<Category> iterator = categories.iterator();
        while (iterator.hasNext()){
            Category next = iterator.next();
            categoryIds.add(next.getId());
        }

        return ServerResponse.createSuccessByData(categoryIds);
    }

    /*
        1   0
        2   1
        3   1
        4   2
        5   3
        6   4

    */

    private Set<Category> findDeepChild(Set<Category> categories, Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category category:categoryList) {
            categories.add(category);
            findDeepChild(categories,category.getId());
        }

        return categories;
    }
}
