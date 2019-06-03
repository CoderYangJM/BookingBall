package com.example.bookingball.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bookingball.ActivityManager;
import com.example.bookingball.MyAcount;
import com.example.bookingball.MyNavigation;
import com.example.bookingball.R;
import com.example.bookingball.adapter.SettleBillAdapter;
import com.example.bookingball.database.SettleBill;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SettleActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView title_txt;
    private TextView unpaid_txt;
    private TextView settle_txt;
    private TextView statistics_txt;
    private RelativeLayout statisticsLayout;
    private RelativeLayout unpaidLayout;
    private RecyclerView recyclerView;
    private MyNavigation navigation;

    private List<SettleBill> settleBillList = new ArrayList<>();
    private SettleBillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);//加入集合
        setContentView(R.layout.activity_settle);
        iniWidget();
        //默认设置滑动菜单
        navigation.defaultSetting();
        navigation.setContext(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        title_txt.setText("已结账单");
        title_txt.setTextColor(Color.BLACK);
        unpaid_txt.setTextColor(Color.BLACK);
        settle_txt.setTextColor(Color.BLUE);
        statistics_txt.setTextColor(Color.BLACK);
        //为底部菜单加上监听器
        addBottomMenuListener();
        //设置RecycleView
        recyclerViewSetting();
    }

    private void recyclerViewSetting() {
        loadSettleBill(); //加载已结账单
        adapter = new SettleBillAdapter(settleBillList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadSettleBill() {
        settleBillList = DataSupport.where("username='" + MyAcount.userName + "'").find(SettleBill.class);
        if (settleBillList.size() > 0) {
            Log.w("SettleActivity", "加载成功");
        } else {
            //从服务器加载
        }
    }

    private void addBottomMenuListener() {

        unpaidLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettleActivity.this, UnpaidActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);//不使用默认切换动画
                finish();
            }
        });
        statisticsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettleActivity.this, StatisticsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        MenuItem addNewBillItem = menu.findItem(R.id.addNewBills);
        addNewBillItem.setVisible(true);
        //隐藏另外两个menu
        MenuItem saveItem = menu.findItem(R.id.save_data);
        saveItem.setVisible(false);
        MenuItem mulItem = menu.findItem(R.id.multiple_choice);
        mulItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//标题栏按钮监听器
        switch (item.getItemId()) {
            case android.R.id.home:
                //打开drawerLayout
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.addNewBills:
                Intent intent = new Intent(this, AddBillActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    private void iniWidget() {
        recyclerView = findViewById(R.id.settle_recyclerView);
        drawerLayout = findViewById(R.id.settle_drawer_layout);
        toolbar = findViewById(R.id.settle_toolbar);
        unpaid_txt = findViewById(R.id.unpaid_text);
        settle_txt = findViewById(R.id.settle_text);
        title_txt = findViewById(R.id.settle_title_text);
        statistics_txt = findViewById(R.id.statistics_text);
        unpaidLayout = findViewById(R.id.unpaid_layout);
        statisticsLayout = findViewById(R.id.statistics_layout);
        navigation = findViewById(R.id.nav_view);
    }

    @Override
    protected void onResume() {
        loadSettleBill();
        adapter.notifyDataSetChanged();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
