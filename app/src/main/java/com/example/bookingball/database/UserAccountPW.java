package com.example.bookingball.database;

import org.litepal.crud.DataSupport;

public class UserAccountPW extends DataSupport {
    private String userName;
    private String password;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "用户:"+userName+",密码:"+password;
    }
}
