package com.spider.dazhong.entity;

import javax.persistence.*;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 21:01
 */
public class ShopCategory {

    private String category;

    private String linkHref;

    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public void setLinkHref(String linkHref) {
        this.linkHref = linkHref;
    }

    @Override
    public String toString() {
        return "ShopCategory{" +
                "category='" + category + 
                ", linkHref='" + linkHref + 
                ", name='" + name + 
                '}';
    }
}
