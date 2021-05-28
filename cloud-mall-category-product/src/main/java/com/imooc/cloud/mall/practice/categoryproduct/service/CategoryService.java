package com.imooc.cloud.mall.practice.categoryproduct.service;

import com.imooc.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.UpdateCategoryReq;
import com.imooc.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;

import java.util.List;

/**
 * 描述： CategoryService接口
 */

public interface CategoryService {

  void add(AddCategoryReq addCategoryReq);

//  void update(Category updateCategoryReq);

  void update(UpdateCategoryReq updateCategoryReq);

  void delete(Integer id);

  PageInfo listForAdmin(Integer pageNum, Integer pageSize);

  List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
