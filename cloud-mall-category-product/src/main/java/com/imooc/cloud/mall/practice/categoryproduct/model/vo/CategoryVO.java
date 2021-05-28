package com.imooc.cloud.mall.practice.categoryproduct.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryVO implements Serializable {
    private Integer id;

    private String name;

    private Integer type;

    private Integer parentId;

    private Integer orderNo;

    private List<CategoryVO> childCategory = new ArrayList<>();

    public List<CategoryVO> getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(List<CategoryVO> childCategory) {
        this.childCategory = childCategory;
    }


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

}