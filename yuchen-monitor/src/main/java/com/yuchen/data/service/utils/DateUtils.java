package com.yuchen.data.service.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @description Date工具类
 * @author: mazhenwei
 * @date: 2022年5月13日
 */
@Log4j2
public class DateUtils {

    /**
     * 根据日期格式获取当前时间，返回String类型
     * @param pattern
     * @return
     */
    public static String getCurrentTimeByPattern(String pattern){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        String format = localDateTime.format(dateTimeFormatter);
        return format;
    }
    public static String getTimeByPattern(String time,String pattern){
        if(StringUtils.isNotBlank(time)&&StringUtils.isNotEmpty(pattern)){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            Date date = parseDate(time,pattern);
            if(date==null){
                return null;
            }
            String format = df.format(date);
            return format;
        }
        return null;
    }
    
    /**
     * 获取过去第几天的日期
     * @param past
     * @param date
     * @return
     */
    public static String getPastDate(int past,Date date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - past);
    	Date today = calendar.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String result =sdf.format(today);
    	return result;
    }
    
    /**
     * 获取当前时间，指定前面多少小时的时间
     * @param ihour
     * @return
     */
	public static String getBeforeByHourTime(int ihour) {
		String returnstr = "";
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - ihour);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		returnstr = df.format(calendar.getTime());
		return returnstr;
	}

    public static String getBeforeByMinuteTime(int minutes) {
        String returnstr = "";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        returnstr = df.format(calendar.getTime());
        return returnstr;
    }

    public static Long getTimestampFromNow(int seconds,int minutes,int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        calendar.add(Calendar.MINUTE, minutes);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTimeInMillis();
    }

    public static String getBeforeByMonthTime(int imonth) {
        String returnstr = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - imonth);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        returnstr = df.format(calendar.getTime());
        return returnstr;
    }

    public static String getBeforeByHourTimestamp(int ihour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - ihour);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.valueOf(calendar.getTimeInMillis());
    }

    public static String getBeforeByDayTimestamp(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - day);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.valueOf(calendar.getTimeInMillis());
    }

    public static Long getTimestamp(int beforeHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - beforeHour);
        return calendar.getTimeInMillis();
    }


    /**
     * 解析格式
     * @param patten
     * @param inputDate
     * @return
     */
    public static Date parseDate(String inputDate,String patten) {
        if(StringUtils.isNotBlank(inputDate)){
            //可能出现的时间格式
                SimpleDateFormat df = new SimpleDateFormat();
                df.applyPattern(patten);
                //设置解析日期格式是否严格解析日期
                df.setLenient(false);
                ParsePosition pos = new ParsePosition(0);
                Date date = df.parse(inputDate, pos);
                if (date != null) {
                    return date;
                }
        }
        return null;
    }
    /**
     * 解析时间
     * @param inputDate
     * @return
     */
    public static Date parseDate(String inputDate) {
        if(StringUtils.isNotBlank(inputDate)){
            //可能出现的时间格式
            String[] patterns = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "yyyy/MM/dd HH:mm:ss",
                    "yyyy/MM/dd HH:mm",
                    "yyyy年MM月dd日",
                    "yyyy-MM-dd",
                    "yyyy/MM/dd",
                    "yyyyMMdd",
                    "yyyy"
            };
            SimpleDateFormat df = new SimpleDateFormat();
            for (String pattern : patterns) {
                df.applyPattern(pattern);
                //设置解析日期格式是否严格解析日期
                df.setLenient(false);
                ParsePosition pos = new ParsePosition(0);
                Date date = df.parse(inputDate, pos);
                if (date != null) {
                    return date;
                }
            }
        }
        return null;
    }

    /**
     * 解析字符串 转换成Date
     * @param date
     * @return
     */
    public static Date strToDateLong(String date){

        Date strToDate=null;

        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strToDate=format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strToDate;
    }

    public static Date strToDate(String date,String patten){

        Date strToDate=null;

        try {
            SimpleDateFormat format=new SimpleDateFormat(patten);
            strToDate=format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strToDate;
    }



    /**
     *    转换成  ISODate
     */
    public static Date dateToISODate(Date date){
        Date  parse= null;
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss.SSS'Z'");
            log.info("============格式化时间为{}",format.format(date));
            parse  = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  parse;
    }
    /**
     *    转换成  ISODate
     */
    public static Date dateToESDate(Date date){
        Date  parse= null;
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("============格式化时间为{}",format.format(date));
            parse  = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  parse;
    }

    /**
     * 时间转换成时间戳,参数和返回值都是字符串
     * @param  s 格式：yyyy-MM-dd
     * @return res
     * @throws ParseException
     */
    public static String dateToStamp(String s) {
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 时间转换成时间戳,参数和返回值都是字符串
     * @param  s 格式：yyyy-MM-dd
     * @return res
     * @throws ParseException
     */
    public static String dateToStamp1(String s) {
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }



    public static void main(String[] args) {
    	String currentTimeMillis = getBeforeByHourTimestamp(0);
    	String one = getBeforeByHourTimestamp(1);
        String two = getBeforeByMonthTime(1);
        String three = getBeforeByDayTimestamp(30);
    	System.out.println(three);
    }

}
