package com.imooc.cloud.mall.practice.categoryproduct.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * UpdateCategoryReq
 */
public class UpdateCategoryReq {

  @NotNull()
  private Integer id;

  @Size(min=2, max=5, message = "名称长度在2至5之间")
  private String name;

  @Max(value = 3, message = "类型值最大为3")
  private Integer type;

  private Integer parentId;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "UpdateCategoryReq{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", type=" + type +
        ", parentId=" + parentId +
        ", orderNo=" + orderNo +
        '}';
  }
}
