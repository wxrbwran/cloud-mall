package com.imooc.cloud.mall.practice.categoryproduct.model.pojo;

import java.io.Serializable;
import java.util.Date;

public class Category implements Serializable {
    private Integer id;

    private String name;

    private Integer type;

    private Integer parentId;

    private Integer orderNo;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Category{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", parentId=" + parentId +
            ", orderNo=" + orderNo +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}