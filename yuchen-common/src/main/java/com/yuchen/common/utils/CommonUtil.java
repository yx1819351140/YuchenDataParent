package com.yuchen.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/23 14:27
 * @Package: com.yuchen.common.utils
 * @ClassName: CommonUtil
 * @Description: 通用工具
 **/
public class CommonUtil {

    public static String bese64Decode(String originStr) {
        byte[] decode = Base64.getDecoder().decode(originStr);
        return new String(decode);
    }

    public static String bese64Encode(String originStr) {
        byte[] encode = Base64.getEncoder().encode(originStr.getBytes());
        return new String(encode);
    }
}
