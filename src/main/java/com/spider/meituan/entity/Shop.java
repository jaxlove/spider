package com.spider.meituan.entity;

import javax.persistence.*;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 21:01
 */
@Table(name = "SHOP")
public class Shop {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopId;

    @Column
    private String shopName;

    @Column
    private String mShopId;

    @Column
    private String categoryName;

    @Column
    private String shopLink;

    @Column
    private String shopPower;

    @Column
    private String regionName;

    @Column
    private String defaultPic;

    public String getmShopId() {
        return mShopId;
    }

    public void setmShopId(String mShopId) {
        this.mShopId = mShopId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getShopLink() {
        return shopLink;
    }

    public void setShopLink(String shopLink) {
        this.shopLink = shopLink;
    }

    public String getShopPower() {
        return shopPower;
    }

    public void setShopPower(String shopPower) {
        this.shopPower = shopPower;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDefaultPic() {
        return defaultPic;
    }

    public void setDefaultPic(String defaultPic) {
        this.defaultPic = defaultPic;
    }
}
