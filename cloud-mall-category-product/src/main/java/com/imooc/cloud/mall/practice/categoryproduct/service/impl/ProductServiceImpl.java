package com.imooc.cloud.mall.practice.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.imooc.cloud.mall.practice.categoryproduct.model.dao.ProductMapper;
import com.imooc.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.imooc.cloud.mall.practice.categoryproduct.model.query.ProductListQuery;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.ProductListReq;
import com.imooc.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.imooc.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.imooc.cloud.mall.practice.categoryproduct.service.ProductService;
import com.imooc.cloud.mall.practice.common.common.Constant;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallException;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 描述： ProductService实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
  @Autowired
  ProductMapper productMapper;

  @Autowired
  CategoryService categoryService;

  @Override
  public void add(AddProductReq addProductReq) {
    Product product = new Product();
    BeanUtils.copyProperties(addProductReq, product);
    Product productOld = productMapper.selectByName(addProductReq.getName());
    if (productOld != null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
    }
    int count = productMapper.insertSelective(product);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
    }
  }

  @Override
  public void update(Product updateProduct) {
    Product productOld = productMapper.selectByName(updateProduct.getName());
    // 同名，不同id，不能修改
    if (productOld != null && productOld.getId().equals(updateProduct.getId())) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
    }
    int count = productMapper.updateByPrimaryKeySelective(updateProduct);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }
  }

  @Override
  public void delete(Integer id) {
    Product productOld = productMapper.selectByPrimaryKey(id);
    if (productOld == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    }
    int count = productMapper.deleteByPrimaryKey(id);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    }
  }

  @Override
  public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
    productMapper.batchUpdateSellStatus(ids, sellStatus);
  }

  @Override
  public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Product> productList = productMapper.selectListForAdmin();
    PageInfo pageInfo = new PageInfo<>(productList);
    return pageInfo;
  }

  @Override
  public PageInfo ListForCustomer(ProductListReq productListReq) {
    // 构建query对象
    ProductListQuery productListQuery = new ProductListQuery();

    String keyword = productListReq.getKeyword();
    // 搜索处理
    if (!StringUtils.isEmptyOrWhitespaceOnly(keyword)) {
      String newKeyword = new StringBuilder().append("%").append(keyword).append("%").toString();
      productListQuery.setKeyword(newKeyword);
    }
    // 目录处理
    // 查询目录及其子目录共同查询，需要目录list
    Integer categoryId = productListReq.getCategoryId();
    if (categoryId != null) {
      List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(categoryId);
      ArrayList<Integer> categoryIds = new ArrayList<>();
      categoryIds.add(categoryId);
      getCategoryIds(categoryVOList, categoryIds);
      productListQuery.setCategoryIds(categoryIds);
    }
    // 排序处理
    String orderBy =  productListReq.getOrderBy();
    if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
      PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
    } else {
      PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
    }
    List<Product> productList = productMapper.selectListForCustomer(productListQuery);
    PageInfo pageInfo = new PageInfo(productList);
    return pageInfo;
  }

  private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
    for (int i = 0; i < categoryVOList.size(); i++) {
      CategoryVO categoryVO = categoryVOList.get(i);
      if (categoryVO != null) {
        categoryIds.add(categoryVO.getId());
        getCategoryIds(categoryVO.getChildCategory(), categoryIds);
      }
    }
  }

  @Override
  public Product detail(Integer id) {
    Product product = productMapper.selectByPrimaryKey(id);
    return product;
  }


}
