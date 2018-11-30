package com.dryork.dc.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
public class DateUtil {
    private static final String DATE_TIME_STR = "yyyy年MM月dd日 HH时mm分ss秒";
    private static final String SQL_DATE_TIME_STR = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_STR);
    private static final SimpleDateFormat sqlDf = new SimpleDateFormat(SQL_DATE_TIME_STR);

    public static String simpleDate(Date date) {
        return df.format(date);
    }

    public static String sqlDatetime(Date date) {
        return sqlDf.format(date);

    }

}
