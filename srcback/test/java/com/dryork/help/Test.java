package com.dryork.help;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 20/11/2018
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private static final Pattern patternCamel = Pattern.compile("_(\\w)");
    private static final Pattern patternUnderline = Pattern.compile("[A-Z]");

    public static void main(String[] args) {
        String str1 = "FermQASDASDSALove";//带下划线的字符串
        StringBuffer sb = underline(str1);
        System.out.println(sb);
    }


    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static StringBuffer camel(String str) {
        //利用正则删除下划线，把下划线后一位改成大写
        Matcher matcher = patternCamel.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if(matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        }else {
            return sb;
        }
        return camel(sb.toString());
    }


    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    public static StringBuffer underline(String str) {
        Matcher matcher = patternUnderline.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if(matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb,"_"+matcher.group(0).toLowerCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        }else {
            return sb;
        }
        return underline(sb.toString());
    }

}
