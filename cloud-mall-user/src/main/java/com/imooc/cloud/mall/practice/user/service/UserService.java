package com.imooc.cloud.mall.practice.user.service;
/**
 * 描述： UserService接口
 */

import com.imooc.cloud.mall.practice.common.exception.ImoocMallException;
import com.imooc.cloud.mall.practice.user.model.pojo.User;

public interface UserService {
  void register(String userName, String password) throws ImoocMallException;

  User login(String userName, String password) throws ImoocMallException;

  void updateInformation(User user) throws ImoocMallException;

  boolean checkAdminRole(User user);
}
