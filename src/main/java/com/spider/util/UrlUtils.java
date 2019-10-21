package com.spider.util;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 19:21
 */
public class UrlUtils {

    public static String getUrl(String url) {
        if (url != null) {
            if (url.substring(0, 7).equalsIgnoreCase("http://")
                    || url.substring(0, 8).equalsIgnoreCase("https://")) {
                return url;
            } else if (url.startsWith("://")) {
                return "https" + url;
            } else if (url.startsWith("//")) {
                return "https:" + url;
            } else {
                return "https://" + url;
            }
        }
        return url;
    }
}
