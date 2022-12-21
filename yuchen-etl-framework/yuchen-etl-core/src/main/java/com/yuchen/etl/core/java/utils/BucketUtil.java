package com.yuchen.etl.core.java.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/19 18:05
 * @Package: com.yuchen.etl.core.java.utils
 * @ClassName: BucketUtil
 * @Description:
 **/
public class BucketUtil {

    public static String process(JSONObject obj, String format) {
        StringBuilder builder = new StringBuilder();
        transform(obj, format, builder);
        return builder.toString();
    }

    private static void transform(JSONObject obj, String format, StringBuilder sb) {
        if (format.contains("/")) {
            String[] split = format.split("/");
            for (String s : split) {
                transform(obj, s, sb);
                sb.append("/");
            }
            sb.delete(sb.length() - 1, sb.length());
        } else {
            String[] meta = StringUtils.remove(StringUtils.remove(format, "("), ")").split("=");
            String key = meta[0];
            String pod = meta[1];
            String[] split = pod.split("\\|");
            String dataKey = null;
            String dateFormat = null;
            if (split.length == 1) {
                dataKey = StringUtils.remove(split[0], "$");
            }
            if (split.length == 2) {
                dataKey = StringUtils.remove(split[0], "$");
                dateFormat = split[1];
            }
            Object o = obj.get(dataKey);
            String value = o.toString();
            if (dateFormat != null) {
                try {
                    value = dateFormat(dateFormat, o);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            sb.append(key + "=" + value);
        }
    }

    public static String dateFormat(String format, Object o) throws ParseException {
        if (StringUtils.isBlank(format) || o == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String value = null;
        if (o instanceof String) {
            Date parse = sdf.parse((String) o);
            value = sdf.format(parse);
        }
        if (o instanceof Date) {
            value = sdf.format(o);
        }

        if (o instanceof Long) {
            Date date = new Date(((Long) o).longValue());
            value = sdf.format(date);
        }
        return value;
    }

}
