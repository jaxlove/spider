package com.spider.util;

import cn.wanghaomiao.xpath.model.JXDocument;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/19 12:32
 */
public abstract class HtmlProccessInterface {

    protected String startUrl;

    protected Map param;

    public abstract List proccess(String html);
    public abstract String formatTargetUrl(String targetUrlPath);

    private void handleHtml(String html, String targetUrlPath, Map map, List list) throws IOException {
        if (StringUtils.isBlank(html)) {
            return;
        }
        List proccess = proccess(html);
        if (proccess != null) {
            list.addAll(proccess);
        }
        Object target = HtmlParseUtil.getInfoByJXDocument(new JXDocument(html), targetUrlPath);
        if (target != null) {
            String ajaxResponse = HttpRequestUtil.getGet2Json(UrlUtils.getUrl(formatTargetUrl(target.toString())), null, map);
            handleHtml(ajaxResponse, targetUrlPath, map, list);
        }
    }

    public List getData(String startUrl, String targetUrlPath, Map map,Map param) throws IOException {
        this.param = param;
        this.startUrl = startUrl;
        String ajaxResponse = HttpRequestUtil.getGet2Json(UrlUtils.getUrl(startUrl), null, map);
        List list = new ArrayList();
        if (ajaxResponse != null) {
            handleHtml(ajaxResponse, targetUrlPath, map, list);
        }
        return list;
    }



}
