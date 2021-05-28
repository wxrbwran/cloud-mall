package com.imooc.cloud.mall.practice.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.imooc.cloud.mall.practice.categoryproduct.model.request.UpdateCategoryReq;
import com.imooc.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.imooc.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.imooc.cloud.mall.practice.common.common.ApiRestResponse;
import com.imooc.cloud.mall.practice.common.common.Constant;
import com.imooc.cloud.mall.practice.user.model.pojo.User;
import com.imooc.cloud.mall.practice.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 目录Controller
 */
@Controller
public class CategoryController {

  @Autowired
  CategoryService categoryService;

  /**
   * @desc 后台添加目录
   * @param addCategoryReq
   * @return ApiRestResponse
   */
  @ApiOperation("后台添加目录")
  @PostMapping("admin/category/add")
  @ResponseBody
  public ApiRestResponse addCategory(HttpSession session,
    @Valid @RequestBody AddCategoryReq addCategoryReq) {
      categoryService.add(addCategoryReq);
    return ApiRestResponse.success();
  }

  @ApiOperation("后台更新目录")
  @PostMapping("admin/category/update")
  @ResponseBody
  public ApiRestResponse updateCategory(
      @Valid @RequestBody UpdateCategoryReq updateCategoryReq) {
      categoryService.update(updateCategoryReq);
      return ApiRestResponse.success();
  }

  @ApiOperation("删除目录")
  @PostMapping("admin/category/delete")
  @ResponseBody
  public ApiRestResponse deleteCategory(@RequestBody Map<String, Integer> map) {
    int id = map.get("id");
    categoryService.delete(id);
    return ApiRestResponse.success();
  }

  @ApiOperation("后台目录列表")
  @GetMapping("admin/category/list")
  @ResponseBody
  public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
    PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
    System.out.println(pageInfo);
    return ApiRestResponse.success(pageInfo);
  }

  @ApiOperation("前台目录列表")
  @GetMapping("category/list")
  @ResponseBody
  public ApiRestResponse listCategoryForCustomer() {
    List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(0);
    return ApiRestResponse.success(categoryVOList);
  }
}
