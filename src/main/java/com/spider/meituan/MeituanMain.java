package com.spider.meituan;

import cn.wanghaomiao.xpath.model.JXDocument;
import com.google.gson.Gson;
import com.spider.meituan.dao.CommentMapper;
import com.spider.meituan.dao.CourseMapper;
import com.spider.meituan.dao.ShopDetailMapper;
import com.spider.meituan.dao.ShopMapper;
import com.spider.meituan.entity.Comment;
import com.spider.meituan.entity.Course;
import com.spider.meituan.entity.Shop;
import com.spider.meituan.entity.ShopDetail;
import com.spider.util.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 18:51
 */
public class MeituanMain {

    private static Jedis jedis = JedisUtils.getJedis();

    public static void main(String[] args) throws Exception {
//        CommentMapper mapper = MyBatisUtil.getMapper(CommentMapper.class);
//        List<Comment> commentList = mapper.selectAll();
//        System.out.println(commentList);

//        insertShop();
        getShopDetail();


    }

    public static void insertShop() throws Exception {
        List<Shop> dataList = getDataList();
        ShopMapper mapper = MyBatisUtil.getMapper(ShopMapper.class);
        for (Shop shop : dataList) {
            mapper.insert(shop);
        }
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
                }else {
                    System.out.println("meituan shopId:"+shop.getmShopId()+"数据已存在,跳过该查询");
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

    public static List<Shop> getDataList() throws Exception {
        String ajaxUrl = "https://i.meituan.com/education/searchMtShopAjax?cityid=56&lat=31.837825&lng=117.13875&pageSize=10&categoryid=20285&fromApp=false&token=&searchParam=%7B%22ctx_utmTerm%22%3A%22%22%2C%22ctx_lat%22%3A%2231.837825%22%2C%22ctx_lng%22%3A%22117.13875%22%2C%22ctx_ci%22%3A%2256%22%2C%22ctx_uuid%22%3A%22e1526cddfd69406eb9b7.1570877727.1.0.0%22%2C%22ctx_userid%22%3A0%2C%22ctx_utmCampaign%22%3A%22%22%2C%22ctx_utmSource%22%3A%22%22%2C%22ctx_utmMedium%22%3A%22ios%22%2C%22ctx_versionName%22%3A%22%22%2C%22ctx_source%22%3A%22H5%22%7D" +
                "&page=";
        List dataList = new ArrayList();
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
                System.out.println((i + "次请求,数据为空，结束请求"));
                break;
            } else if (json.get("code") == null || String.valueOf(json.get("code")).indexOf("200") == -1) {
                System.out.println(i + "次请求失败=========" + ajaxResponse);
            } else {
                List<Map> result = (List) ((Map) json.get("msg")).get("result");
                if (result != null && !result.isEmpty()) {
                    for (Map o : result) {
                        String shopId = new BigDecimal(o.get("shopId").toString()).toPlainString();
                        System.out.println("meituan shopId : " + shopId);
                        o.put("mShopId", shopId);
                        o.remove("shopId");
                        Shop shop = gson.fromJson(gson.toJson(o), Shop.class);
                        dataList.add(shop);
                    }
                } else {
                    System.out.println(i + "次请求,数据为空,结束请求");
                    break;
                }
            }
        }
        return dataList;
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
        Object avg = HtmlParseUtil.getInfoByDocument(jxDocument, "//div[@class='rating']/span[@class='avg-price']/text()");
        Object addressMap = HtmlParseUtil.getInfoByDocument(jxDocument, "//h6[@class='address-block']/a[@class='react']/@href");
        Object address = HtmlParseUtil.getInfoByDocument(jxDocument, "//h6[@class='address-block']/a[@class='react']/div[@class='poi-address']/text()");
        Object phone = HtmlParseUtil.getInfoByDocument(jxDocument, "//div/p/a[@data-com='phonecall']/@data-tele");
        List<Element> courseList = HtmlParseUtil.getList(jxDocument, "//dl[@class='list']//dl[@class='list bd-deal-list']//dd");
        Object commentUrl = HtmlParseUtil.getInfoByDocument(jxDocument, "//dd[@class='buy-comments db']/a[@class='react']/@href");
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
                Object courseName = HtmlParseUtil.getInfoByDocument(new JXDocument(element.html()), "//a[@class='react ']/@title");
                Object img = HtmlParseUtil.getInfoByDocument(new JXDocument(element.html()), "//div[@class='dealcard-img imgbox']/@data-src-high");
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
