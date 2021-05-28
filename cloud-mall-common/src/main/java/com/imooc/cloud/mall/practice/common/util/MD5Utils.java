package com.imooc.cloud.mall.practice.common.util;

import com.imooc.cloud.mall.practice.common.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：   MD5工具
 */
public class MD5Utils {
  public static String getMD5String(String strValue) throws NoSuchAlgorithmException {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    return Base64.encodeBase64String(md5.digest((strValue + Constant.SALT).getBytes(StandardCharsets.UTF_8)));
  }
  // 测试生成的md5的值
  public static void main(String[] args) {
    String md5 = null;
    try {
      md5 = getMD5String("1234"+ Constant.SALT);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    System.out.println(md5);
  }
}
