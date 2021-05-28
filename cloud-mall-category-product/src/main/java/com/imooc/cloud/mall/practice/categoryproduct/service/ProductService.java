package com.imooc.cloud.mall.practice.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import com.imooc.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.ProductListReq;

/**
 * 商品服务接口
 */
public interface ProductService {
  void add(AddProductReq addProductReq);

  void update(Product updateProduct);

  void delete(Integer id);

  void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

  PageInfo listForAdmin(Integer pageNum, Integer pageSize);

  PageInfo ListForCustomer(ProductListReq productListReq);

  Product detail(Integer id);
}
