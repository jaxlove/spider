package com.spider;

import cn.wanghaomiao.xpath.model.JXDocument;
import com.google.gson.Gson;
import com.spider.util.HttpRequestUtil;
import com.spider.util.UrlUtils;
import org.seimicrawler.xpath.exception.NoSuchAxisException;
import org.seimicrawler.xpath.exception.NoSuchFunctionException;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;

import java.io.IOException;
import java.util.*;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/9 20:53
 */
public class Test {
    private static String param = "&cid=20285&cateType=poi&stid_b=3";

    public static void main(String[] args) throws Exception {
        test2();
    }

    public static void test2() throws IOException {
        Map map = new HashMap();
        map.put("Accept-Encoding","identity");
        map.put("pragma-device","863504037015010");
        map.put("pragma-newtoken","13_LjpQJs_gmV-0OcyDjkVvU4rwAAAAAJQkAAH8IISPUM9BJjJzUm2LtSLfqOwmiNRkR0NG8khidbT4mq-KAA4D4quW5IaT7JImU-Q");
        map.put("M-SHARK-TRACEID","101EEFEB1FB7AE9FAB82FB1A451FBF0F7C99E665B3490B2D660AEF5022D9343A715857a0815715501045103c1bc4");
        map.put("pragma-token","13_LjpQJs_gmV-0OcyDjkVvU4rwAAAAAJQkAAH8IISPUM9BJjJzUm2LtSLfqOwmiNRkR0NG8khidbT4mq-KAA4D4quW5IaT7JImU-Q");
        map.put("pragma-unionid","3b6acfddfa2d488b85564c09284536510000000000001028673");
        map.put("User-Agent","MApi 1.3 (com.sankuai.meituan 10.3.401 vivo6 vivo_X9; Android 7.1.2)");
        map.put("utm-source","vivo6");
        map.put("utm-term","1000030401");
        map.put("network-type","wifi");
        map.put("pragma-os","MApi 1.3 (com.sankuai.meituan 10.3.401 vivo6 vivo_X9; Android 7.1.2)");
        map.put("pragma-uuid","EEFEB1FB7AE9FAB82FB1A451FBF0F7C99E665B3490B2D660AEF5022D9343A715");
        map.put("utm-campaign","AgroupBgroupC0D100E0Ghomepage_category12_20285__a1__c-1024");
        map.put("utm-medium","android");
        map.put("utm-content","863504037015010");
        map.put("Host","mapi.meituan.com");

        String get2Json = HttpRequestUtil.getGet2Json("https://mapi.meituan.com/general/platform/mtlist/categorynavilist.bin?start=15&selected=true&wifiaddress=d8%3Ac8%3Ae9%3A42%3A6c%3A38&wifiname=610.5&wifistrong=-46&cityid=56&categoryid=20285&lat=31.81921923410759&lng=117.17646980456426&wificonnect=true&utm_source=vivo6&utm_medium=android&utm_term=1000030401&version_name=10.3.401&utm_content=863504037015010&utm_campaign=AgroupBgroupC0D100E0Ghomepage_category12_20285__a1__c-1024&ci=56&uuid=EEFEB1FB7AE9FAB82FB1A451FBF0F7C99E665B3490B2D660AEF5022D9343A715&token=13_LjpQJs_gmV-0OcyDjkVvU4rwAAAAAJQkAAH8IISPUM9BJjJzUm2LtSLfqOwmiNRkR0NG8khidbT4mq-KAA4D4quW5IaT7JImU-Q&userid=127418314",
                "", map);
        System.out.println(get2Json);
    }

    public static void testHtml() throws IOException, NoSuchFunctionException, XpathSyntaxErrorException, NoSuchAxisException {
        String htmlUrl = "http://i.meituan.com/hefei/all/?"+param;
        Map map = new HashMap();
        map.put("Content-Type", "application/json;charset=UTF-8");
        map.put("x-forwarded-for", "183.232.231.174");
        List<String> data = new ArrayList<>();
        getData(data, htmlUrl, "//span[@class='poiname']/text()", "//a[@gaevent='imt/deal/list/pageNext']/@href");
        String[] strings = new String[data.size()];
        data.toArray(strings);
        System.out.println(strings);
    }

    private static void getData(List<String> data, String firstUrl, String dataXpath, String targetXpath) throws IOException {
        Map map = new HashMap();
        map.put("Content-Type", "application/json;charset=UTF-8");
        map.put("x-forwarded-for", "183.232.231.174");
        String firstUrlHtml = HttpRequestUtil.getGet2Json(firstUrl, null, map);
        data = Objects.isNull(data) ? new ArrayList<>() : data;
        JXDocument jxDocument = new JXDocument(firstUrlHtml);
        List<Object> rs = jxDocument.sel(dataXpath);
        for (Object o : rs) {
            data.add(o.toString());
        }
        List<Object> targetList = jxDocument.sel(targetXpath);
        if (targetList != null && !targetList.isEmpty()) {
            for (Object o : targetList) {
                getData(data, UrlUtils.getUrl(o.toString())+param, dataXpath, targetXpath);
            }
        }
    }


    public static void testAjax() throws IOException {
        String ajaxUrl = "https://i.meituan.com/education/searchMtShopAjax?cityid=56&lat=31.837825&lng=117.13875&pageSize=10&categoryid=20285&fromApp=false&token=&searchParam=%7B%22ctx_utmTerm%22%3A%22%22%2C%22ctx_lat%22%3A%2231.837825%22%2C%22ctx_lng%22%3A%22117.13875%22%2C%22ctx_ci%22%3A%2256%22%2C%22ctx_uuid%22%3A%22e1526cddfd69406eb9b7.1570877727.1.0.0%22%2C%22ctx_userid%22%3A0%2C%22ctx_utmCampaign%22%3A%22%22%2C%22ctx_utmSource%22%3A%22%22%2C%22ctx_utmMedium%22%3A%22ios%22%2C%22ctx_versionName%22%3A%22%22%2C%22ctx_source%22%3A%22H5%22%7D" +
                "&page=";

        Gson gson = new Gson();
        for (int i = 1; i < 1000; i++) {
            System.out.println("正在进行第" + i + "次请求");
            Map map = new HashMap();
            map.put("Content-Type", "application/json;charset=UTF-8");
            map.put("x-forwarded-for", "183.232.231.174");
            map.put("Referer", "https://i.meituan.com/jiaoyupeixun/channel?stid_b=3&cevent=homepage%2Fcategory1%2F20285");
            map.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
            String ajaxResponse = HttpRequestUtil.getGet2Json(ajaxUrl + i, null, map);
            Map json = gson.fromJson(ajaxResponse, Map.class);
            if (json == null || json.isEmpty()) {
                System.out.println("数据为空");
                return;
            } else if (json.get("code") == null || String.valueOf(json.get("code")).indexOf("200") == -1) {
                System.out.println("code为失败=========" + ajaxResponse);
                return;
            } else {
                Object shopId = json.get("107614115");
                Object shopName = json.get("shopName");
                Object categoryName = json.get("categoryName");
                Object shopLink = json.get("shopLink");
                Object shopPower = json.get("shopPower");
                Object regionName = json.get("regionName");
            }
        }
    }


}
