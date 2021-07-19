package com.imooc.cloud.mall.practice.cartorder.controller;

import com.github.pagehelper.PageInfo;

import com.imooc.cloud.mall.practice.cartorder.service.OrderService;
import com.imooc.cloud.mall.practice.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单后台管理Controller
 */
@RestController
public class OrderAdminController {
  @Autowired
  OrderService orderService;

  @GetMapping("admin/order/list")
  @ApiOperation("管理员订单列表")
  public ApiRestResponse listForAdmin(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
    PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
    return ApiRestResponse.success(pageInfo);
  }



  /**
   * 发货 订单状态流程： 0：用户已取消 10：未付款 20：已付款 30：已发货 40：交易完成
   * @param orderNo
   * @return
   */
  @PostMapping("admin/order/delivered")
  @ApiOperation("管理员发货")
  public ApiRestResponse delivered(String orderNo) {
    orderService.delivered(orderNo);
    return ApiRestResponse.success();
  }

  /**
   * 完结订单 管理员及用户都可调用
   * @param orderNo
   * @return
   */
  @PostMapping("order/finish")
  @ApiOperation("完结订单")
  public ApiRestResponse finish(String orderNo) {
    orderService.finish(orderNo);
    return ApiRestResponse.success();
  }
}
