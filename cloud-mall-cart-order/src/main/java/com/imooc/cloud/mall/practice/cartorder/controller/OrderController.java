package com.imooc.cloud.mall.practice.cartorder.controller;

import com.github.pagehelper.PageInfo;

import com.imooc.cloud.mall.practice.cartorder.model.request.CreateOrderReq;
import com.imooc.cloud.mall.practice.cartorder.model.vo.OrderVO;
import com.imooc.cloud.mall.practice.cartorder.service.OrderService;
import com.imooc.cloud.mall.practice.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单Controller
 */
@RestController
public class OrderController {
  @Autowired
  OrderService orderService;

  @PostMapping("order/create")
  @ApiOperation("创建订单")
  public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
    String orderNo = orderService.create(createOrderReq);
    return ApiRestResponse.success(orderNo);
  }

  @GetMapping("order/detail")
  @ApiOperation("前台订单详情")
  public ApiRestResponse detail(@RequestParam String orderNo) {
    OrderVO orderVO = orderService.detail(orderNo);
    return ApiRestResponse.success(orderVO);
  }


  @GetMapping("order/list")
  @ApiOperation("前台订单列表")
  public ApiRestResponse list(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
    PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
    return ApiRestResponse.success(pageInfo);
  }

  @PostMapping("order/cancel")
  @ApiOperation("前台取消订单")
  public ApiRestResponse cancel(String orderNo) {
    orderService.cancel(orderNo);
    return ApiRestResponse.success();
  }

  @GetMapping("order/qrcode")
  @ApiOperation("生成二维码")
  public ApiRestResponse qrcode(String orderNo) {
    String pngAddress = orderService.qrcode(orderNo);
    return ApiRestResponse.success(pngAddress);
  }

  @PostMapping("pay")
  @ApiOperation("支付接口")
  public ApiRestResponse pay(String orderNo) {
    orderService.pay(orderNo);
    return ApiRestResponse.success();
  }
}
