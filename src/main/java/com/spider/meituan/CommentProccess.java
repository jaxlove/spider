package com.spider.meituan;

import cn.wanghaomiao.xpath.model.JXDocument;
import com.spider.meituan.entity.Comment;
import com.spider.util.HtmlProccessInterface;
import com.spider.util.JXDocumentUtil;
import com.spider.util.StringUtil;
import com.spider.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/19 12:57
 */
public class CommentProccess extends HtmlProccessInterface {

    @Override
    public List proccess(String html) {
        JXDocument jxDocument = new JXDocument(html);
        List list = JXDocumentUtil.getList(jxDocument, "//div[@class='feedbackCard']");
        if (list != null && !list.isEmpty()) {
            list = (List) list.stream().map(t -> {
                Element e = (Element) t;
                Object cotent = JXDocumentUtil.getInfoByJXDocument(new JXDocument(e.html()), "//div[@class='comment']//text()");
                Object time = JXDocumentUtil.getInfoByJXDocument(new JXDocument(e.html()), "//div[@class='user-info-text']//div[@class='score']/weak/text()");
                List imgs = JXDocumentUtil.getList(new JXDocument(e.html()), "//div[@class='pics']//span/@data-src");
                List starsFull = JXDocumentUtil.getList(new JXDocument(e.html()),
                        "//div[@class='user-info-text']//div[@class='score']/span[@class='stars']/img[@class='icn_star star_full']");
                Comment comment = new Comment();
                comment.setShopId(Long.valueOf(this.param.get("shopId").toString()));
                if (starsFull != null) {
                    comment.setStars(String.valueOf(starsFull.size()));
                } else {
                    comment.setStars("0");
                }
                if (cotent != null) {
                    comment.setContent(StringUtil.removeNonBmpUnicode(cotent.toString()));
                }
                if (time != null) {
                    comment.setCommentTime(time.toString());
                }
                if (imgs != null && !imgs.isEmpty()) {
                    imgs = (List) imgs.stream().map(img -> {
                        if (img != null) {
                            return UrlUtils.getUrl(img.toString());
                        }
                        return "";
                    }).collect(Collectors.toList());
                    comment.setImgs(StringUtils.join(imgs.toArray(), "!@#"));
                }
                return comment;
            }).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public String formatTargetUrl(String targetUrlPath) {
        return this.startUrl + "/page_" + targetUrlPath;
    }
}
