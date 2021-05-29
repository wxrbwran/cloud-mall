package com.imooc.cloud.mall.practice.cartorder.service.impl;

/**
 * 描述： 购物车实现类
 */


import com.imooc.cloud.mall.practice.cartorder.feign.ProductFeignClient;
import com.imooc.cloud.mall.practice.cartorder.model.dao.CartMapper;
import com.imooc.cloud.mall.practice.cartorder.model.pojo.Cart;
import com.imooc.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.imooc.cloud.mall.practice.cartorder.service.CartService;
import com.imooc.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.imooc.cloud.mall.practice.common.common.Constant;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallException;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
  @Autowired
  ProductFeignClient productFeignClient;

  @Autowired
  CartMapper cartMapper;

  @Override
  public List<CartVO> list(Integer userId) {
    List<CartVO> cartVOS = cartMapper.selectList(userId);
    for (CartVO cartVO : cartVOS) {
      cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
    }
    return cartVOS;
  }

  @Override
  public List<CartVO> update(Integer userId, Integer productId, Integer count) {
    validProduct(productId, count);
    Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
    if (cart == null) {
      // 未添加过购物车，新增一个记录
      throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    } else {
      cart.setQuantity(count);
      cartMapper.updateByPrimaryKeySelective(cart);
    }
    return this.list(userId);
  }

  @Override
  public List<CartVO> delete(Integer userId, Integer productId) {
    Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
    if (cart == null) {
      // 未添加过购物车，无法删除
      throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    } else {
      cartMapper.deleteByPrimaryKey(cart.getId());
    }
    return this.list(userId);
  }

  @Override
  public List<CartVO> add(Integer userId, Integer productId, Integer count) {
    validProduct(productId, count);
    Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
    if (cart == null) {
      // 未添加过购物车，新增一个记录
      cart = new Cart();
      cart.setProductId(productId);
      cart.setUserId(userId);
      cart.setQuantity(count);
      cart.setSelected(Constant.Cart.CHECKED);
      cartMapper.insertSelective(cart);
    } else {
      // 已添加过购物车，则数量相加
      cart.setQuantity(cart.getQuantity() + count);
      cartMapper.updateByPrimaryKeySelective(cart);
    }
    return this.list(userId);
  }


  private void validProduct(Integer productId, Integer count) {
    Product product = productFeignClient.detailForFeign(productId);
    // 判断商品是否存在及上架
    if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
    }
    // 判断商品库存
    if (count > product.getStock()) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
    }
  }

  @Override
  public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
    Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
    if (cart == null) {
      //这个商品之前不在购物车里，无法选择/不选中
      throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    } else {
      //这个商品已经在购物车里了，则可以选中/不选中
      cartMapper.selectOrNot(userId,productId,selected);
    }
    return this.list(userId);
  }

  @Override
  public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
    //改变选中状态
    cartMapper.selectOrNot(userId, null, selected);
    return this.list(userId);
  }
}
