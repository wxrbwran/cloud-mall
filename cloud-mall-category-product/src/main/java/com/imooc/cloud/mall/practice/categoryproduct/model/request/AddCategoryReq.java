package com.imooc.cloud.mall.practice.categoryproduct.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * AddCategoryReq
 */
public class AddCategoryReq {

  @Size(min=2, max=5, message = "名称长度在2至5之间")
  @NotNull(message = "名称不能为null")
  private String name;

  @NotNull(message = "type不能为null")
  @Max(value = 3, message = "类型值最大为3")
  private Integer type;

  @NotNull(message = "parentId不能为null")
  private Integer parentId;

  @NotNull(message = "订单量不能为空")
  private Integer orderNo;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public Integer getorderNo() {
    return orderNo;
  }

  public void setorderNo(Integer orderNo) {
    this.orderNo = orderNo;
  }

  @Override
  public String toString() {
    return "AddCategoryReq{" +
        "name='" + name + '\'' +
        ", type=" + type +
        ", parentId=" + parentId +
        ", orderNo=" + orderNo +
        '}';
  }
}
