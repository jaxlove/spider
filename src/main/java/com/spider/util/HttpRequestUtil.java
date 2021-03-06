package com.spider.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/14 19:07
 */
public class HttpRequestUtil {

    public static String getPost4Json(String url, String json) throws Exception {
        Map map = new HashMap();
        map.put("Content-Type","application/json;charset=UTF-8");
        return getPost4Json(url,json,map);
    }

    public static String getPost4Json(String url, String json, Map<String,String> head) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
        httpPost.setConfig(defaultRequestConfig);
        if(head != null && !head.isEmpty()){
            head.forEach((key,value)->httpPost.addHeader(key, value));
        }
        httpPost.setEntity(new StringEntity(json, "UTF-8"));
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
        }
        return result;
    }

    public static String getGet2Json(String url,String param,Map<String,String> head) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
        httpGet.setConfig(defaultRequestConfig);
        if(head != null && !head.isEmpty()){
            head.forEach((key,value)->httpGet.addHeader(key, value));
        }
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "GB2312");
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
        }
        return result;
    }
}
