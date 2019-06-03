package com.example.bookingball.database;

import org.litepal.crud.DataSupport;

import java.io.Serializable;


public class UnpaidBill extends DataSupport implements Serializable {
    private int id;
    private double total; //欠款总额度
    private double returned; //已归还额度
    private String name;   //借出/入 对象的名字
    private String type;  //表示类型
    private String limitDate; //限还日期
    private String createDate; //创建日期
    private String remarks;//备注
    private String tel;   //对象联系方式
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getId() {
        return id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getReturned() {
        return returned;
    }

    public void setReturned(double returned) {
        this.returned = returned;
    }

    public String getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(String limitDate) {
        this.limitDate = limitDate;
    }

    @Override
    public String toString() {
        return "UnpaidBill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", total=" + total +
                ", returned=" + returned +
                ", limitDate=" + limitDate +
                ", remarks='" + remarks + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
