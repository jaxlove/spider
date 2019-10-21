package com.spider.meituan.entity;

import org.apache.ibatis.annotations.Update;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 21:20
 */
@Table(name = "COMMENT")
public class Comment {

    @Id
    @Column
    private Long commentId;

    @Column
    private Long shopId;

    @Column
    private String content;

    @Column
    private String commentTime;

    @Column
    private String stars;

    //以!@#隔开
    @Column
    private String imgs;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getContent() {
        return content;
    }

    @Update("set names utf8mb4")
    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }
}
