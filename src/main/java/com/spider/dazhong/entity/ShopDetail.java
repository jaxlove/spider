package com.spider.dazhong.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 21:03
 */
@Table(name = "SHOP_DETAIL")
public class ShopDetail {

    @Id
    @Column
    private Long shopDetailId;

    @Column
    private Long shopId;

    @Column
    private String avg;

    @Column
    private String address;

    @Column
    private String addressMap;

    @Column
    private String phone;

    public Long getShopDetailId() {
        return shopDetailId;
    }

    public void setShopDetailId(Long shopDetailId) {
        this.shopDetailId = shopDetailId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressMap() {
        return addressMap;
    }

    public void setAddressMap(String addressMap) {
        this.addressMap = addressMap;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
