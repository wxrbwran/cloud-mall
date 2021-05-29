package com.imooc.cloud.mall.practice.cartorder.feign;

import com.imooc.cloud.mall.practice.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * user的feign client
 */
@FeignClient(value="cloud-mall-user")
public interface UserFeignClient {

  @PostMapping("getUser")
  public User getUser();

  @PostMapping("checkAdminRole")
  public Boolean checkAdminRole(User user);
}
