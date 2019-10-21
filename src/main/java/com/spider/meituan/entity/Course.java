package com.spider.meituan.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 21:09
 */
@Table(name = "COURSE")
public class Course {

    @Id
    @Column
    private Long courseId;

    @Column
    private Long shopId;

    @Column
    private String name;

    @Column
    private String img;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
