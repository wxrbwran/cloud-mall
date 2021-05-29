package com.imooc.cloud.mall.practice.cartorder.feign;

import com.imooc.cloud.mall.practice.categoryproduct.model.pojo.Product;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品的feign client
 */
@FeignClient(value="cloud-mall-category-product")
public interface ProductFeignClient {

  @GetMapping("product/detailForFeign")
  public Product detailForFeign(@RequestParam Integer id);

  @PostMapping("product/updateStock")
  public void updateStock(@RequestParam Integer productId, @RequestParam Integer stock);
}
