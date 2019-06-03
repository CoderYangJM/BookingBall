package com.example.bookingball;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bookingball.activitys.LoginActivity;
import com.example.bookingball.activitys.UpdatePWActivity;

public class MyNavigation extends NavigationView {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public MyNavigation(Context context) {
        super(context);
    }

    public MyNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void defaultSetting() {
        this.setItemIconTintList(null);
        this.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_my_info:
                        //查看个人信息
                        Toast.makeText(context, "此功能成在开发中", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_modify_pw:
                        //修改密码
                        Intent intent = new Intent(context, UpdatePWActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        //退出登录
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("提醒");
                        dialog.setMessage("是否确定退出登录");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityManager.removeAll();//结束所有活动
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
