package com.imooc.cloud.mall.practice.common.common;

import com.google.common.collect.Sets;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallException;
import com.imooc.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 描述： 常量
 */
@Component
public class Constant {
  public final static String SALT = "k'a4a/4s3d$%^&1_qe.a)s{d";
  public final static String IMOOC_MALL_USER = "imooc_mall_user";
  public static String FILE_UPLOAD_DIR;

  @Value("${file.upload.dir}")
  public void setFileUploadDir(String fileUploadDir) {
    FILE_UPLOAD_DIR = fileUploadDir;
  }

  public interface ProductListOrderBy{
    Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
//    Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");

  }

  public interface SaleStatus {
    int NOT_SALE = 0; // 商品下架状态
    int SALe = 1; // 商品上架状态
  }

  public interface Cart {
    int UN_CHECKED = 0; // 购物车未选中状态
    int CHECKED = 1; // 选中状态
  }

  public enum OrderStatusEnum {
    CANCELED("用户已取消", 0),
    NOT_PAID("未付款", 10),
    PAID("已付款", 20),
    DELIVERED("已发货", 30),
    FINISHED("交易完成", 40);

    private String value;
    private  int code;

    OrderStatusEnum(String value, int code) {
      this.value = value;
      this.code = code;
    }

    public static OrderStatusEnum codeOf(int code) {
      for(OrderStatusEnum orderStatusEnum: values()) {
        if (orderStatusEnum.getCode() == code) {
          return orderStatusEnum;
        }
      }
      throw new ImoocMallException(ImoocMallExceptionEnum.NO_ENUM);
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }
  }

}
