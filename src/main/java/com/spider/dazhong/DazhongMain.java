package com.spider.dazhong;

import cn.wanghaomiao.xpath.model.JXDocument;
import com.google.gson.Gson;
import com.spider.dazhong.dao.CommentMapper;
import com.spider.dazhong.dao.CourseMapper;
import com.spider.dazhong.dao.ShopDetailMapper;
import com.spider.dazhong.dao.ShopMapper;
import com.spider.dazhong.entity.*;
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

    public static void main(String[] args) throws Exception {
//        CommentMapper mapper = MyBatisUtil.getMapper(CommentMapper.class);
//        List<Comment> commentList = mapper.selectAll();
//        System.out.println(commentList);

        insertShop();
//        getShopDetail();


    }

    public static void insertShop() throws Exception {
        List<ShopCategory> second = getSecond();
        if (second != null && !second.isEmpty()) {
            for (ShopCategory shopCategory : second) {
                List<Shop> list = getList(shopCategory);
                if (list == null || list.isEmpty()) {
                    logger.error("类型为：" + shopCategory.getCategory() + "获取数据为空");
                }
            }
        } else {
            logger.error("获取二级类型为空！");
        }
        ShopMapper mapper = MyBatisUtil.getMapper(ShopMapper.class);
    }

    public static List<Shop> getList(ShopCategory shopCategory) throws IOException {
        if (StringUtils.isNotBlank(shopCategory.getLinkHref())) {
            List<Shop> returnList = new ArrayList<>();
            getList(shopCategory.getLinkHref(), shopCategory.getCategory(), returnList, "//a[@class='next']/@href");
            return returnList;
        }
        return null;
    }

    public static void getList(String url, String category, List list, String targetPath) throws IOException {
        String get2Json = HttpRequestUtil.getGet2Json(url, null, getMapHead());
        List<Element> listElement = HtmlParseUtil.getList(get2Json, "//div[@class='shop-list J_shop-list shop-all-list']/ul/li");
        if (list != null && !list.isEmpty()) {
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
        Object infoByHtml = HtmlParseUtil.getInfoByHtml(get2Json, targetPath);
        if (infoByHtml != null) {
            getList(StringUtil.getString(infoByHtml), category, list, targetPath);
        }else {
            System.out.println("类型"+category+"爬取结束");
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
        Map map = getMapHead();
        String get2Json = HttpRequestUtil.getGet2Json(ajaxUrl, null, map);
        List<Element> list = HtmlParseUtil.getList(get2Json, "//div[@class='sec-items']//a[@class='second-item']");
        List<ShopCategory> result = new ArrayList<>();
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
        return result;
    }

    private static Map getMapHead() {
        Map map = new HashMap();
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        map.put("Cache-Control", "max-age=0");
        map.put("Connection", "keep-alive");
        map.put("Cookie", "navCtgScroll=200; _lxsdk_cuid=16a452ac559c8-0d47f0b3b06736-551e3f12-100200-16a452ac55bc8; _lxsdk=16a452ac559c8-0d47f0b3b06736-551e3f12-100200-16a452ac55bc8; Hm_lvt_e6f449471d3527d58c46e24efb4c343e=1555938592; _hc.v=3b464c0a-f02b-c4d9-13b4-9b833d411937.1555938592; lgtoken=05024e20d-becd-4c1b-aeb1-c14c88363810; cy=110; cye=hefei; Hm_lvt_4c4fc10949f0d691f3a2cc4ca5065397=1571661524; Hm_lpvt_4c4fc10949f0d691f3a2cc4ca5065397=1571661524; s_ViewType=10; _lxsdk_s=16dee539885-aad-ee8-4c6%7C%7C55");
        map.put("Host", "www.dianping.com");
        map.put("Referer", "http://www.dianping.com/hefei/ch75");
        map.put("Upgrade-Insecure-Requests", "1");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
        return map;
    }

}
