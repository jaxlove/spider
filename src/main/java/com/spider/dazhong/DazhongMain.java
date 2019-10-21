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
import org.jsoup.nodes.Element;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

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

        ShopMapper mapper = MyBatisUtil.getMapper(ShopMapper.class);
    }

    public static void getShopDetail() throws Exception {
        List<Shop> dataList = getDataFromDba();
        if (dataList != null && !dataList.isEmpty()) {
            for (Shop shop : dataList) {
                if (jedis.get(shop.getmShopId()) == null) {
                    System.err.println("shopId:" + shop.getShopId() + "开始爬取");
                    Thread.sleep(1);
                    if (StringUtils.isNotBlank(shop.getShopLink())) {
                        Map<String, Object> detail = getDetail(shop.getShopId(), shop.getShopLink());
                        ShopDetail shopDetail = (ShopDetail) detail.get("shopDetail");
                        MyBatisUtil.getMapper(ShopDetailMapper.class).insertSelective(shopDetail);
                        List<Course> courseList = (List<Course>) detail.get("courses");
                        if (courseList != null) {
                            CourseMapper mapper = MyBatisUtil.getMapper(CourseMapper.class);
                            for (Course course : courseList) {
                                mapper.insertSelective(course);
                            }
                        }
                        List<Comment> commentList = (List<Comment>) detail.get("comment");
                        if (commentList != null) {
                            CommentMapper mapper = MyBatisUtil.getMapper(CommentMapper.class);
                            for (Comment comment : commentList) {
                                mapper.insertSelective(comment);
                            }
                        }
                        jedis.set(shop.getmShopId(), "1");
                    } else {
                        System.out.println("shopId:" + shop.getShopId() + " shopLink为空,跳过");
                    }
                } else {
                    System.out.println("meituan shopId:" + shop.getmShopId() + "数据已存在,跳过该查询");
                }
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
        String get2Json = HttpRequestUtil.getGet2Json(ajaxUrl, null, map);
        List<Element> list = HtmlParseUtil.getList(get2Json, "//div[@class='sec-items']//a[@class='second-item']");
        List<ShopCategory> result = new ArrayList<>();
        for (Element element : list) {
            ShopCategory shopCategory = new ShopCategory();
            Object href = HtmlParseUtil.getInfoByJXDocument(element.html(), "//@href");
            Object category = HtmlParseUtil.getInfoByJXDocument(element.html(), "//@data-category");
            Object categoryName = HtmlParseUtil.getInfoByJXDocument(element.html(), "//text()");
            shopCategory.setCategory(StringUtil.getString(category));
            shopCategory.setLinkHref(StringUtil.getString(href));
            shopCategory.setName(StringUtil.getString(categoryName));
            result.add(shopCategory);
        }
        return result;
    }

    public static Map<String, Object> getDetail(Long shopId, String url) throws Exception {
        Gson gson = new Gson();
        Map map = new HashMap();
        map.put("Referer", url);
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        map.put("Connection", "keep-alive");
        map.put("Cookie", "JSESSIONID=xepwa05ggsql1ohhs3sje7wo3; IJSESSIONID=xepwa05ggsql1ohhs3sje7wo3; iuuid=43FFC4C3B2DFADC4805777F52F4B91BDBB833161669D4D2EC518A4A288BDE9F4; latlng=31.78204%2C117.228065%2C1571463280881; ci=56; cityname=%E5%90%88%E8%82%A5; nodown=yes; _lxsdk_cuid=16de282c50ec8-09b576fb043568-2d604637-4a574-16de282c50fc8; _lxsdk=43FFC4C3B2DFADC4805777F52F4B91BDBB833161669D4D2EC518A4A288BDE9F4; _lxsdk_s=16de282c405-720-d7f-707%7C%7C2; i_extend=H__a100037__b1; __utma=74597006.737165552.1571463284.1571463284.1571463284.1; __utmc=74597006; __utmz=74597006.1571463284.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); idau=1; __utmb=74597006.2.9.1571463329668");
        map.put("x-forwarded-for", "183.232.231.174");
        map.put("Sec-Fetch-Mode", "navigate");
        map.put("Sec-Fetch-Site", "cross-site");
        map.put("Sec-Fetch-User", "?1");
        map.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        String html = HttpRequestUtil.getGet2Json(UrlUtils.getUrl(url), null, map);
        JXDocument jxDocument = new JXDocument(html);
        Object avg = HtmlParseUtil.getInfoByJXDocument(jxDocument, "//div[@class='rating']/span[@class='avg-price']/text()");
        Object addressMap = HtmlParseUtil.getInfoByJXDocument(jxDocument, "//h6[@class='address-block']/a[@class='react']/@href");
        Object address = HtmlParseUtil.getInfoByJXDocument(jxDocument, "//h6[@class='address-block']/a[@class='react']/div[@class='poi-address']/text()");
        Object phone = HtmlParseUtil.getInfoByJXDocument(jxDocument, "//div/p/a[@data-com='phonecall']/@data-tele");
        List<Element> courseList = HtmlParseUtil.getList(jxDocument, "//dl[@class='list']//dl[@class='list bd-deal-list']//dd");
        Object commentUrl = HtmlParseUtil.getInfoByJXDocument(jxDocument, "//dd[@class='buy-comments db']/a[@class='react']/@href");
        Map detail = new HashMap();
        detail.put("avg", avg);
        detail.put("shopId", shopId);
        detail.put("addressMap", addressMap);
        detail.put("address", address);
        detail.put("phone", phone);
        detail.put("avg", avg);
        Map result = new HashMap();
        ShopDetail shopDetail = gson.fromJson(gson.toJson(detail), ShopDetail.class);
        result.put("shopDetail", shopDetail);
        System.out.println(gson.toJson(shopDetail));
        List<Course> courses = new ArrayList<>();
        if (courseList != null && !courseList.isEmpty()) {
            for (Element element : courseList) {
                Object courseName = HtmlParseUtil.getInfoByJXDocument(new JXDocument(element.html()), "//a[@class='react ']/@title");
                Object img = HtmlParseUtil.getInfoByJXDocument(new JXDocument(element.html()), "//div[@class='dealcard-img imgbox']/@data-src-high");
                Map courseMap = new HashMap();
                courseMap.put("shopId", shopId);
                courseMap.put("name", courseName);
                if (img != null) {
                    courseMap.put("img", UrlUtils.getUrl(img.toString()));
                }
                Course course = gson.fromJson(gson.toJson(courseMap), Course.class);
                courses.add(course);
            }
        }
        result.put("courses", courses);
        if (commentUrl != null) {
            Map param = new HashMap();
            param.put("shopId", shopId);
            List<Comment> data = new CommentProccess().getData(commentUrl.toString(), "//a[@gaevent='imt/deal/feedbacklist/pageNext']/@data-page-num", map, param);
            result.put("comment", data);
        } else {
            System.out.println(shopId + "=== 没有评论");
        }
        return result;
    }


}
