package com.spider.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/9 20:58
 */
public class HttpClientUtil {

    public static void main(String[] args) {

        // 实例化Web客户端、①模拟 Chrome 浏览器 ✔ 、②使用代理IP ✔
//        WebClient webClient = new WebClient(BrowserVersion.CHROME, "118.114.77.47", 8080);
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false); // 取消 CSS 支持 ✔
        webClient.getOptions().setJavaScriptEnabled(false); // 取消 JavaScript支持 ✔
        try {
            HtmlPage page = webClient.getPage("http://i.meituan.com/poi/178763473"); // 解析获取页面

            /**
             * Xpath:级联选择 ✔
             * ① //：从匹配选择的当前节点选择文档中的节点，而不考虑它们的位置
             * ② h3：匹配<h3>标签
             * ③ [@class='company_name']：属性名为class的值为company_name
             * ④ a：匹配<a>标签
             */
            List<HtmlElement> spanList = page.getByXPath("//dd/div/h1[@class='dealcard-brand']");

            for (int i = 0; i < spanList.size(); i++) {
                //asText ==> innerHTML ✔
                System.out.println(i + 1 + "、" + spanList.get(i).asText());
            }

        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webClient.close(); // 关闭客户端，释放内存
        }
    }

}
