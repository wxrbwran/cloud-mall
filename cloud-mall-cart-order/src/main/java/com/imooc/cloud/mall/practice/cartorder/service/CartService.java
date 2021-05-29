package com.imooc.cloud.mall.practice.cartorder.service;


import com.imooc.cloud.mall.practice.cartorder.model.vo.CartVO;

import java.util.List;


public interface CartService {
  List<CartVO> list(Integer userId);

  List<CartVO> delete(Integer userId, Integer productId);

  List<CartVO> add(Integer userId, Integer productId, Integer count);

  List<CartVO> update(Integer id, Integer productId, Integer count);

  List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

  List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
