package com.imooc.cloud.mall.practice.cartorder.controller;


import com.imooc.cloud.mall.practice.cartorder.feign.UserFeignClient;
import com.imooc.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.imooc.cloud.mall.practice.cartorder.service.CartService;
import com.imooc.cloud.mall.practice.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车Controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {
  @Autowired
  CartService cartService;


  @Autowired
  UserFeignClient userFeignClient;
  @ApiOperation("购物车列表")
  @GetMapping("/list")
  public ApiRestResponse list() {
    // 内部获取用户Id，防止横向越权
//    System.out.println(userFeignClient.getUser().getId());
    List<CartVO> cartVOList = cartService.list(userFeignClient.getUser().getId());
    return ApiRestResponse.success(cartVOList);
  }

  @ApiOperation("添加商品到购物车")
  @PostMapping("/add")
  public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
    List<CartVO> cartVOList = cartService.add(userFeignClient.getUser().getId(), productId, count);
    return ApiRestResponse.success(cartVOList);
  }

  @ApiOperation("更新购物车")
  @PostMapping("/update")
  public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
    List<CartVO> cartVOList = cartService.update(userFeignClient.getUser().getId(), productId, count);
    return ApiRestResponse.success(cartVOList);
  }

  @ApiOperation("删除购物车")
  @PostMapping("/delete")
  public ApiRestResponse delete(@RequestParam Integer productId) {
    List<CartVO> cartVOList = cartService.delete(userFeignClient.getUser().getId(), productId);
    return ApiRestResponse.success(cartVOList);
  }

  @PostMapping("/select")
  @ApiOperation("选择/不选择购物车的某商品")
  public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
    //不能传入userID，cartID，否则可以删除别人的购物车
    List<CartVO> cartVOList = cartService
        .selectOrNot(userFeignClient.getUser().getId(), productId, selected);
    return ApiRestResponse.success(cartVOList);
  }

  @PostMapping("/selectAll")
  @ApiOperation("全选择/全不选择购物车的某商品")
  public ApiRestResponse selectAll(@RequestParam Integer selected) {
    //不能传入userID，cartID，否则可以删除别人的购物车
    List<CartVO> cartVOList = cartService
        .selectAllOrNot(userFeignClient.getUser().getId(), selected);
    return ApiRestResponse.success(cartVOList);
  }
}
