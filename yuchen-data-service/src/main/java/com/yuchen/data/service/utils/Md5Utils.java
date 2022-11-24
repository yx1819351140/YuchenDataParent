package com.yuchen.data.service.utils;

import java.security.MessageDigest;

/**
 * @author gzz
 * @date 2022/3/117:15
 * @description
 */

public class Md5Utils {
    private static final String SLAT = "&$%511123333***&&%%$$#@123";

    public static String encrypt(String str){
        try {
            str = str + SLAT;
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            byte[] s = messageDigest.digest();
            String result = "";
            for(int i = 0;i < s.length;i++){
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){

        String str = encrypt("admin");
    }
}
