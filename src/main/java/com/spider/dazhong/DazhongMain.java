package com.spider.dazhong;

import com.spider.dazhong.dao.ShopMapper;
import com.spider.dazhong.entity.Shop;
import com.spider.dazhong.entity.ShopCategory;
import com.spider.dazhong.util.DazhongUtil;
import com.spider.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 18:51
 */
public class DazhongMain {

    private static Logger logger = Logger.getLogger(DazhongMain.class);

    private static Jedis jedis = JedisUtils.getJedis();

    private static int IP_INDEX = 0;

    private static String IPS[] = {"14.215.177.38", "14.215.177.39", "180.96.1.179", "36.255.193.88", "185.53.178.6", "47.90.116.65"};

    public static void main(String[] args) throws Exception {
//        CommentMapper mapper = MyBatisUtil.getMapper(CommentMapper.class);
//        List<Comment> commentList = mapper.selectAll();
//        System.out.println(commentList);

        insertShop();
//        getShopDetail();


    }

    public static void insertShop() throws Exception {
        ShopMapper mapper = MyBatisUtil.getMapper(ShopMapper.class);
        List<ShopCategory> second = getSecond();
        if (second != null && !second.isEmpty()) {
            for (ShopCategory shopCategory : second) {
                logger.error("类型为：" + shopCategory.getCategory() + "开始爬取");
                List<Shop> list = getList(shopCategory);
                if (list == null || list.isEmpty()) {
                    logger.error("类型为：" + shopCategory.getCategory() + "获取数据为空");
                } else {
                    for (Shop shop : list) {
                        mapper.insert(shop);
                    }
                }
            }
        } else {
            logger.error("获取二级类型为空！");
        }

    }

    public static List<Shop> getList(ShopCategory shopCategory) throws IOException, InterruptedException {
        if (StringUtils.isNotBlank(shopCategory.getLinkHref())) {
            List<Shop> returnList = new ArrayList<>();
            getList(shopCategory.getLinkHref(), shopCategory.getCategory(), returnList, "//a[@class='next']/@href");
            return returnList;
        }
        return null;
    }

    public static void getList(String url, String category, List list, String targetPath) throws IOException, InterruptedException {
        Thread.sleep(500);
        Map<String, String> getResponse = HttpRequestUtil.getGet2Json(url, getMapHead());
        if ("200".equals(getResponse.get("code"))) {
            List<Element> listElement = HtmlParseUtil.getList(getResponse.get("result"), "//div[@class='shop-list J_shop-list shop-all-list']/ul/li");
            if (listElement != null && !listElement.isEmpty()) {
                for (Element element : listElement) {
                    Shop shop = new Shop();
                    Object img = HtmlParseUtil.getInfoByHtml(element.html(), "//div[@class='pic']//img/@data-src");
                    Object shopUrl = HtmlParseUtil.getInfoByHtml(element.html(), "//div[@class='pic']/a/@href");
                    Object name = HtmlParseUtil.getInfoByHtml(element.html(), "//div[@class='txt']/div[@class='tit']//h4/text()");
                    Object shopId = HtmlParseUtil.getInfoByHtml(element.html(), "//div[@class='txt']/div[@class='tit']/a/@data-shopid");
                    Object powerClass = HtmlParseUtil.getInfoByHtml(element.html(), "//div[@class='comment']/span/@class");
                    shop.setDefaultPic(StringUtil.getString(img));
                    shop.setShopLink(StringUtil.getString(shopUrl));
                    shop.setCategory(category);
                    shop.setShopName(StringUtil.getString(name));
                    shop.setmShopId(StringUtil.getString(shopId));
                    shop.setShopPower(DazhongUtil.getPower(powerClass));
                    logger.info(shop);
                    list.add(shop);
                }
            }
            Object infoByHtml = HtmlParseUtil.getInfoByHtml(getResponse.get("result"), targetPath);
            if (infoByHtml != null) {
                getList(StringUtil.getString(infoByHtml), category, list, targetPath);
            } else {
                System.out.println("类型" + category + "爬取结束");
            }
        }

    }


    public static List<Shop> getDataFromDba() {
        ShopMapper mapper = MyBatisUtil.getMapper(ShopMapper.class);
        Example example = new Example(Shop.class);
        example.setOrderByClause("shop_id");
        return mapper.selectByExample(example);
    }

    public static List<ShopCategory> getSecond() throws Exception {
        String ajaxUrl = "http://www.dianping.com/hefei/ch75/g2872";
        Map<String, String> getResponse = HttpRequestUtil.getGet2Json(ajaxUrl, getMapHead());
        List<ShopCategory> result = new ArrayList<>();
        if ("200".equals(getResponse.get("code"))) {
            List<Element> list = HtmlParseUtil.getList(getResponse.get("result"), "//div[@class='sec-items']//a[@class='second-item']");
            for (Element element : list) {
                ShopCategory shopCategory = new ShopCategory();
                Object href = element.attr("href");
                Object category = element.attr("data-category");
                Object categoryName = element.text();
                shopCategory.setCategory(StringUtil.getString(category));
                shopCategory.setLinkHref(StringUtil.getString(href));
                shopCategory.setName(StringUtil.getString(categoryName));
                logger.info(shopCategory);
                result.add(shopCategory);
            }
        }

        return result;
    }

    private static Map getMapHead() {

        Map map = new HashMap();
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        map.put("Cache-Control", "max-age=0");
        map.put("Connection", "keep-alive");
        map.put("Host", "www.dianping.com");
        map.put("Referer", "http://www.dianping.com/hefei/ch75");
        map.put("Upgrade-Insecure-Requests", "1");
        if (map.get("Cookie") == null) {
            map.put("Cookie", "s_ViewType=10; _lxsdk_cuid=16df798f15fc8-05e1cc6f573506-b363e65-144000-16df798f15f13; _lxsdk=16df798f15fc8-05e1cc6f573506-b363e65-144000-16df798f15f13; _hc.v=3b5516c9-9314-2990-4c2f-548d1ab12592.1571817059; dper=64a75d477fe715550e9e724662670010cee7e11a9ac0e4fc5a7ced377221c1fc9d4a3d90952e0cd430399674285e44cdcaf1eae30a67a13e28ee07e692f511d762ff7165e0d13483fd4c61ef008d72bd646307ffb896ec4820b0adf124a9bbdf; ua=dpuser_0864550663; ctu=8da876039202fc99ac3b98de6881110818f71fb4bc062cc11129dff70b95c01f; ll=7fd06e815b796be3df069dec7836c3df; _lxsdk_s=16dfd3e6c5e-005-2b2-03a%7C%7C141");
        }
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36");

        String ip = IPS[IP_INDEX++ / IPS.length];
        map.put("X-Real-IP", ip);
        map.put("x-forwarded-for", ip);
        map.put("Proxy-Client-IP", ip);
        map.put("WL-Proxy-Client-IP", ip);
        return map;
    }

}
