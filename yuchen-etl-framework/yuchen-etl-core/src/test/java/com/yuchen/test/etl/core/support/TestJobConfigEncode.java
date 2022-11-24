package com.yuchen.test.etl.core.support;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/23 13:35
 * @Package: com.yuchen.test.etl.core.support
 * @ClassName: TestJobConfigEncode
 * @Description:
 **/
public class TestJobConfigEncode {
    public static void main(String[] args) {
        String msg = "ewogICAgImpvYk5hbWUiOiAiU2VhcmNoRGVmYXVsdERhdGEiLAogICAgInN0cmVhbUR1cmF0aW9uIjogMTAwMDAsCiAgICAiZW5hYmxlSGl2ZVN1cHBvcnQiOiB0cnVlLAogICAgImlzTG9jYWwiOiBmYWxzZSwKICAgICJzcGFya0NvbmZpZyI6IHsKICAgICAgICAic3Bhcmsuc3FsLnNvdXJjZXMucGFydGl0aW9uT3ZlcndyaXRlTW9kZSI6ICJEWU5BTUlDIiwKICAgICAgICAiaGl2ZS5leGVjLmR5bmFtaWMucGFydGl0aW9uIjogInRydWUiLAogICAgICAgICJoaXZlLmV4ZWMuZHluYW1pYy5wYXJ0aXRpb24ubW9kZSI6ICJub25zdHJpY3QiLAogICAgICAgICJzcGFyay5zcWwuYnJvYWRjYXN0VGltZW91dCI6ICIxMDAwIiwKICAgICAgICAic3Bhcmsuc3FsLnNvdXJjZXMucGFydGl0aW9uT3ZlcndyaXRlTW9kZSI6ICJkeW5hbWljIiwKICAgICAgICAic3BhcmsuZGVidWcubWF4VG9TdHJpbmdGaWVsZHMiOiAiMTAwMCIKICAgIH0sCiAgICAiam9iQ29uZmlnIjogewogICAgICAgICJ0YWJsZU5hbWUiOiAibXlfdGVzdF8yMDIyMTEyMiIKICAgIH0KfQ==";
        byte[] base64decodedBytes = Base64.getDecoder().decode(msg);
        System.out.println("原始字符串: " + new String(base64decodedBytes));
    }
}
