package com.spider.util;

import net.sourceforge.htmlunit.cyberneko.parsers.DOMFragmentParser;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/14 19:55
 */
public class ExtractorUtil {
    /**
     * 加载文件(xml，html等)，获得DOMtree对象
     *
     * @param filePath 文件的路径
     * @return domTree
     */


    public static DocumentFragment getDomtree(String filePath) {
        StringBuffer content = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return getDomtree(content.toString(), "utf-8");
    }

    /**
     * 将html，xml字符串转换为Dom对象
     *
     * @param html
     * @param encode
     * @return domTree
     */


    public static DocumentFragment getDomtree(String html, String encode) {
        byte[] byt = html.getBytes();
        InputSource source = null;
        InputStreamReader isr = null;
        try {
            source = new InputSource();
            isr = new InputStreamReader(new ByteArrayInputStream(byt), encode);
            source.setCharacterStream(isr);
            DOMFragmentParser domParser = new DOMFragmentParser();
            DocumentFragment domtree = new HTMLDocumentImpl()
                    .createDocumentFragment();
            domParser.parse(source, domtree);
            return domtree;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    /**
     * 获取html中xpath的String 文本
     *
     * @param xpath
     * @return
     */


    public static String getTextContentByXpath(DocumentFragment domtree, String xpath) {
        Node node = null;
        try {
            node = XPathAPI.selectSingleNode(domtree, xpath);
        } catch (TransformerException e) {
            return null;
        }
        if (node != null) {
            return node.getTextContent();
        } else {
            return null;
        }
    }

    /**
     * 获取html中xpath的String 文本
     *
     * @param html
     * @param xpath
     * @param encode
     * @return
     */


    public static List<String> getTextContentListByXpath(
            DocumentFragment domtree, String xpath) {
        List<String> result = new ArrayList<String>();
        NodeList nodeList = null;
        try {
            nodeList = XPathAPI.selectNodeList(domtree, xpath);
        } catch (TransformerException e) {
            return null;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            result.add(node.getTextContent());
        }
        return result;
    }

    /**
     * 过滤出数字
     *
     * @param input
     * @return 返回字符串里的数字，如果没有数字返回0，用来回去评论数和转发数，粉丝数，关注数，博文数等
     */


    public static int getNumber(String input) {
        try {
            return Integer.parseInt(input.replaceAll("\\D", ""));
        } catch (Exception e) {
            return 0;
        }
    }


    public static void main(String[] args) {
        DocumentFragment domtree = ExtractorUtil.getDomtree("conf/url.xml");
        List<String> list = ExtractorUtil.getTextContentListByXpath(domtree,
                "//BOARDCONFIGS/URL");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
