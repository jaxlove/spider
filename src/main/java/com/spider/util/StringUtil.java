package com.spider.util;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/19 16:23
 */
public class StringUtil {

    public static String removeNonBmpUnicode(String str) {
        if (str == null) {
            return null;
        }
        str = str.replaceAll("[^\\u0000-\\uFFFF]", "");
        return str;
    }

    public static String getString(Object object) {
        if (object == null) {
            return null;
        } else {
            return object.toString();
        }
    }
}
