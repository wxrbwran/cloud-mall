package com.imooc.cloud.mall.practice.zuul.filter;

import com.imooc.cloud.mall.practice.common.common.Constant;
import com.imooc.cloud.mall.practice.user.model.pojo.User;
import com.imooc.cloud.mall.practice.zuul.feign.UserFeignClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 用户校验过滤器
 */
@Component
public class AdminFilter extends ZuulFilter {

  @Autowired
  UserFeignClient userFeignClient;

  @Override
  public String filterType() {
    return FilterConstants.PRE_TYPE;
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    String requestURI =  request.getRequestURI();
    if (requestURI.contains("adminLogin")) {
      return false;
    }
    return requestURI.contains("admin");
  }

  @Override
  public Object run() throws ZuulException {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    HttpSession session = request.getSession();
    User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
    if (currentUser == null) {
      ctx.setSendZuulResponse(false);
      ctx.setResponseBody("{\n" +
          "    \"status\": 10007,\n" +
          "    \"msg\": \"用户未登录\",\n" +
          "    \"data\": null\n" +
          "}");
      ctx.setResponseStatusCode(200);
      return null;
    }

    // 校验是否是管理员
    if (!userFeignClient.checkAdminRole(currentUser)) {
      ctx.setSendZuulResponse(false);
      ctx.setResponseBody("{\n" +
          "    \"status\": 10009,\n" +
          "    \"msg\": \"需要管理员权限\",\n" +
          "    \"data\": null\n" +
          "}");
      ctx.setResponseStatusCode(200);
    }
    return null;
  }
}
