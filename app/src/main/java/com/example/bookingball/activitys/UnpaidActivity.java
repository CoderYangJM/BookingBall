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
import com.example.bookingball.adapter.UnpaidBillAdapter;
import com.example.bookingball.database.UnpaidBill;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class UnpaidActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView title_txt;
    private TextView unpaid_txt;
    private TextView settle_txt;
    private TextView statistics_txt;
    private RelativeLayout settleLayout;
    private RelativeLayout statisticsLayout;
    private RecyclerView recyclerView;
    private MyNavigation navigationView;


    private List<UnpaidBill> unpaidBillList = new ArrayList<>();
    private UnpaidBillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);//加入集合
        setContentView(R.layout.activity_unpaid);
        iniWidget();
        navigationView.defaultSetting();
        navigationView.setContext(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        title_txt.setText("未结账单");
        title_txt.setTextColor(Color.BLACK);
        unpaid_txt.setTextColor(Color.BLUE);
        settle_txt.setTextColor(Color.BLACK);
        statistics_txt.setTextColor(Color.BLACK);
        navigationView.defaultSetting();
        //为底部菜单加上监听器
        addBottomMenuListener();
        loadUnpaidBills();
        //recyclerView实例化并设置账单适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UnpaidBillAdapter(unpaidBillList);
        recyclerView.setAdapter(adapter);
    }

    private void addBottomMenuListener() {
        settleLayout.setClickable(true);
        settleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnpaidActivity.this, SettleActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        statisticsLayout.setClickable(true);
        statisticsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnpaidActivity.this, StatisticsActivity.class);
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
                addNewAccounts();
                break;
            default:
                break;
        }
        return true;
    }

    public void addNewAccounts() {
        //跳转新建账目界面
        Intent intent = new Intent(UnpaidActivity.this, AddBillActivity.class);
        startActivity(intent);
    }

    private void iniWidget() {
        recyclerView = findViewById(R.id.unpaid_recyclerView);
        drawerLayout = findViewById(R.id.unpaid_drawer_layout);
        toolbar = findViewById(R.id.unpaid_toolbar);
        unpaid_txt = findViewById(R.id.unpaid_text);
        settle_txt = findViewById(R.id.settle_text);
        title_txt = findViewById(R.id.unpaid_title_text);
        statistics_txt = findViewById(R.id.statistics_text);
        settleLayout = findViewById(R.id.settle_layout);
        statisticsLayout = findViewById(R.id.statistics_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void loadUnpaidBills() {
        //优先从数据库加载
        Log.w("UnpaidActivity", "开始加载");
        unpaidBillList = DataSupport.where("username='" + MyAcount.userName+"'").find(UnpaidBill.class);
        if (unpaidBillList.size() > 0) {
            Log.w("UnpaidActivity", "加载成功");
        } else {
            //从服务器加载
        }
    }


    @Override
    protected void onResume() {
        loadUnpaidBills();
        adapter.notifyDataSetChanged(); //刷新一次适配器
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
