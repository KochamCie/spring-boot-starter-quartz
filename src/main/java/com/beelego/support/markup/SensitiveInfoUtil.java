package com.beelego.support.markup;


import java.util.Date;

/**
 * @author : hama
 * @since : created in  2018/7/2
 */

public class SensitiveInfoUtil {

    public static String dateMarkup(final Date date) {
        return DateUtil.format(date);
    }
}
