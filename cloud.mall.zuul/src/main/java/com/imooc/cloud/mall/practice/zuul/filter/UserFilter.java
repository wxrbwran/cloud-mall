package com.imooc.cloud.mall.practice.zuul.filter;

import com.imooc.cloud.mall.practice.common.common.Constant;
import com.imooc.cloud.mall.practice.user.model.pojo.User;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;


/**
 * 用户校验过滤器
 */
public class UserFilter extends ZuulFilter {
//  public static User currentUser;

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
    if (requestURI.contains("images") || requestURI.contains("pay")) {
      return false;
    }
    return requestURI.contains("cart") || requestURI.contains("order");
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
    }
    return null;
  }
}
