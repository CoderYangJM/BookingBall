package com.example.bookingball.database;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class SettleBill extends DataSupport implements Serializable {
    private int id;
    private String name;
    private String type;
    private String createTime;
    private String tel;
    private double total;
    private String remarks;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remark) {
        this.remarks = remark;
    }

    @Override
    public String toString() {
        return "SettleBill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", createTime='" + createTime + '\'' +
                ", tel='" + tel + '\'' +
                ", total=" + total +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
