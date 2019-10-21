package com.spider.util;

import cn.wanghaomiao.xpath.model.JXDocument;

import java.util.List;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/19 12:33
 */

public class JXDocumentUtil {

    public static Object getInfoByJXDocument(JXDocument jxDocument, String xpath) {
        List<Object> avgList = jxDocument.sel(xpath);
        if (avgList != null && avgList.size() > 0) {
            return avgList.get(0);
        }
        return null;
    }

    public static List getList(JXDocument jxDocument, String xpath) {
        List<Object> list = jxDocument.sel(xpath);
        return list;
    }

}
