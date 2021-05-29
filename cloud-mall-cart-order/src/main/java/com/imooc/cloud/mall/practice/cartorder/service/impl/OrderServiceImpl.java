package com.imooc.cloud.mall.practice.cartorder.service.impl;

/**
 * 描述： 购物车实现类
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.imooc.cloud.mall.practice.cartorder.feign.ProductFeignClient;
import com.imooc.cloud.mall.practice.cartorder.feign.UserFeignClient;
import com.imooc.cloud.mall.practice.cartorder.model.dao.CartMapper;
import com.imooc.cloud.mall.practice.cartorder.model.dao.OrderItemMapper;
import com.imooc.cloud.mall.practice.cartorder.model.dao.OrderMapper;
import com.imooc.cloud.mall.practice.cartorder.model.pojo.Order;
import com.imooc.cloud.mall.practice.cartorder.model.pojo.OrderItem;
import com.imooc.cloud.mall.practice.cartorder.model.request.CreateOrderReq;
import com.imooc.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.imooc.cloud.mall.practice.cartorder.model.vo.OrderItemVO;
import com.imooc.cloud.mall.practice.cartorder.model.vo.OrderVO;
import com.imooc.cloud.mall.practice.cartorder.service.CartService;
import com.imooc.cloud.mall.practice.cartorder.service.OrderService;
import com.imooc.cloud.mall.practice.cartorder.util.OrderCodeFactory;
import com.imooc.cloud.mall.practice.common.util.QRCodeGenerator;
import com.imooc.cloud.mall.practice.categoryproduct.common.ProductConstant;
import com.imooc.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.imooc.cloud.mall.practice.common.common.Constant;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallException;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  CartService cartService;

  @Autowired
  UserFeignClient userFeignClient;

  @Autowired
  ProductFeignClient productFeignClient;

  @Autowired
  CartMapper cartMapper;

  @Autowired
  OrderMapper orderMapper;

  @Autowired
  OrderItemMapper orderItemMapper;

  @Value("${file.upload.ip}")
  String ip;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreateOrderReq createOrderReq) {
    // 拿到用户id
    Integer userId = userFeignClient.getUser().getId();
    // 购物车以勾选商品
    List<CartVO> cartVOList = cartService.list(userId);
    ArrayList<CartVO> cartVOArrayListTemp = new ArrayList<>();
    for (CartVO cartVO : cartVOList) {
      if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
        cartVOArrayListTemp.add(cartVO);
      }
    }
    cartVOList = cartVOArrayListTemp;
    // 如果勾选为空，报错
    if (CollectionUtils.isEmpty(cartVOList)) {
      throw new ImoocMallException(ImoocMallExceptionEnum.CART_EMPTY);
    }
    // 判断商品是否存在，上下架状态，库存
    validSaleStatusAndStock(cartVOList);
    // 把购物车对象转为item对象
    List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
    // 扣库存
    for (OrderItem orderItem : orderItemList) {
      Product product = productFeignClient.detailForFeign(orderItem.getProductId());
      int stock = product.getStock() - orderItem.getQuantity();
      if (stock < 0) {
        throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
      }
      product.setStock(stock);
      productFeignClient.updateStock(product.getId(), stock);
    }
    // 把购物车中的以勾选商品删除
    cleanCart(cartVOList);
    // 生成订单
    Order order = new Order();
    // 生成订单号，有自己的规则
    String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
    order.setOrderNo(orderNo);
    order.setUserId(userId);
    order.setTotalPrice(totalPrice(orderItemList));
    order.setReceiverName(createOrderReq.getReceiverName());
    order.setReceiverMobile(createOrderReq.getReceiverMobile());
    order.setReceiverAddress(createOrderReq.getReceiverAddress());
    order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
    order.setPostage(0);
    order.setPaymentType(1);
    // 插入到order表
    orderMapper.insertSelective(order);
    // 循环保存每个商品到order_item表
    for (OrderItem orderItem : orderItemList) {
      orderItem.setOrderNo(order.getOrderNo());
      orderItemMapper.insertSelective(orderItem);
    }
    // 把结果返回
    return orderNo;
  }

  private Integer totalPrice(List<OrderItem> orderItemList) {
    Integer totalPrice = 0;
    for (OrderItem orderItem : orderItemList) {
      totalPrice += orderItem.getTotalPrice();
    }
    return totalPrice;
  }

  private void cleanCart(List<CartVO> cartVOList) {
    for (CartVO cartVO : cartVOList) {
      cartMapper.deleteByPrimaryKey(cartVO.getId());
    }
  }

  private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
    List<OrderItem> orderItemList = new ArrayList<>();
    for (int i = 0; i < cartVOList.size(); i++) {
      CartVO cartVO = cartVOList.get(i);
      OrderItem orderItem = new OrderItem();
      orderItem.setProductId(cartVO.getProductId());
      // 记录商品快照信息
      orderItem.setProductName(cartVO.getProductName());
      orderItem.setProductImg(cartVO.getProductImage());
      orderItem.setUnitPrice(cartVO.getPrice());
      orderItem.setQuantity(cartVO.getQuantity());
      orderItem.setTotalPrice(cartVO.getTotalPrice());
      orderItemList.add(orderItem);
    }
    return orderItemList;
  }

  private void validSaleStatusAndStock(List<CartVO> cartVOList) {
    for (CartVO cartVO : cartVOList) {
      Integer productId = cartVO.getProductId();
      Integer count = cartVO.getQuantity();
      Product product = productFeignClient.detailForFeign(productId);
//      Product product = null;
      // 判断商品是否存在及上架
      if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
        throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
      }
      // 判断商品库存
      if (count > product.getStock()) {
        throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
      }
    }
  }

  /**
   *
   * @param orderNo
   * @return OrderVO
   */
  @Override
  public OrderVO detail(String orderNo) {
    Order order = orderMapper.selectByOrderNo(orderNo);
    // 订单不存在
    if (order == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
    }
    // 订单存在，判断订单所属
    Integer userId = userFeignClient.getUser().getId();
    if (!userId.equals(order.getUserId())) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
    }
    OrderVO orderVO = getOrderVO(order);
    return orderVO;
  }


  private OrderVO getOrderVO(Order order) {
    OrderVO orderVO = new OrderVO();
    BeanUtils.copyProperties(order, orderVO);
    //获取对应的orderItemVOList
    List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
    List<OrderItemVO> orderItemVOList = new ArrayList<>();
    for (OrderItem orderItem : orderItemList) {
      OrderItemVO orderItemVO = new OrderItemVO();
      BeanUtils.copyProperties(orderItem, orderItemVO);
      orderItemVOList.add(orderItemVO);
    }
    orderVO.setOrderItemVOList(orderItemVOList);
    orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
    return orderVO;
  }

  @Override
  public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
    Integer userId = userFeignClient.getUser().getId();
    PageHelper.startPage(pageNum, pageSize);
    List<Order> orderList = orderMapper.selectForCustom(userId);
    List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
    PageInfo pageInfo = new PageInfo<>(orderList);
    pageInfo.setList(orderVOList);
    return pageInfo;
  }

  private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
    List<OrderVO> orderVOList = new ArrayList<>();
    for (int i = 0; i < orderList.size(); i++) {
      Order order = orderList.get(i);
      OrderVO orderVO = getOrderVO(order);
      orderVOList.add(orderVO);
    }
    return orderVOList;
  }

  @Override
  public void cancel(String orderNo) {
    Order order = orderMapper.selectByOrderNo(orderNo);
    // 查不到订单，报错
    if(order == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
    }
    // 验证用户身份
    Integer userId = userFeignClient.getUser().getId();
    if (!userId.equals(order.getUserId())) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
    }
    if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
      order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
      order.setEndTime(new Date());
      orderMapper.updateByPrimaryKeySelective(order);
    } else {
      throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
    }
  }

  @Override
  public String qrcode(String orderNo) {
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    HttpServletRequest request = servletRequestAttributes.getRequest();
    String address = ip + ":" + request.getLocalPort();
    String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
    try {
      QRCodeGenerator.generateQRCodeImage(payUrl, 350, 350, ProductConstant.FILE_UPLOAD_DIR + orderNo+ ".png");
    } catch (WriterException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String pngAddress = "http://" + address +"/images/" + orderNo +".png";
    return  pngAddress;
  }

  @Override
  public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Order> orderList = orderMapper.selectAllForAdmin();
    List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
    PageInfo pageInfo = new PageInfo<>(orderList);
    pageInfo.setList(orderVOList);
    return pageInfo;
  }

  @Override
  public void pay(String orderNo) {
    Order order = orderMapper.selectByOrderNo(orderNo);
    // 订单不存在
    if (order == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
    }

    if(order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()) {
      order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
      order.setEndTime(new Date());
      orderMapper.updateByPrimaryKeySelective(order);
    } else {
      throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
    }
  }

  /**
   * 发货
   * @param orderNo
   */
  @Override
  public void delivered(String orderNo) {
    Order order = orderMapper.selectByOrderNo(orderNo);
    // 订单不存在
    if (order == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
    }
    if(order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()) {
      order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
      order.setDeliveryTime(new Date());
      orderMapper.updateByPrimaryKeySelective(order);
    } else {
      throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
    }
  }

  /**
   * 完结订单
   * @param orderNo
   */
  @Override
  public void finish(String orderNo) {
    Order order = orderMapper.selectByOrderNo(orderNo);
    // 订单不存在
    if (order == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
    }
    // 普通用户 校验订单所属
    // 订单存在，判断订单所属
    Integer userId = userFeignClient.getUser().getId();
    if (!userFeignClient.checkAdminRole(userFeignClient.getUser()) && !userId.equals(order.getUserId())) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
    }
    // 发货后 可以完结订单
    if(order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()) {
      order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
      order.setEndTime(new Date());
      orderMapper.updateByPrimaryKeySelective(order);
    } else {
      throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
    }
  }
}
