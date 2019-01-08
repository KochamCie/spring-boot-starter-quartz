package com.beelego.support.markup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: rns
 * @Date: 2019/1/6 下午10:37
 * @Description: DateUtil
 */
public class DateUtil {


    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_DATE = "1970-01-01 00:00:00";

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>(){

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN);
        }
    };

    public static String format(Date date){
        if(null == date){
            return DEFAULT_DATE;
        }
        return sdf.get().format(date);
    }

    public static Date parse(String dateStr) throws ParseException {
        return sdf.get().parse(dateStr);
    }




}
