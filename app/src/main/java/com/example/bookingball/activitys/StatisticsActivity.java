package com.example.bookingball.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bookingball.ActivityManager;
import com.example.bookingball.MyAcount;
import com.example.bookingball.MyNavigation;
import com.example.bookingball.R;
import com.example.bookingball.database.SettleBill;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class StatisticsActivity extends AppCompatActivity {
    private final String[] years = new String[101];
    private final String[] months = new String[12];
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> yearsAdapter;
    private ArrayAdapter<String> monthsAdapter;
    //控件
    private Toolbar toolbar;
    private TextView title_text;
    private MyNavigation navigationView;
    private Spinner yearSpinner;
    private LineChart yearLineChart;
    private LineChart monthLineChart;
    private RadarChart radarChart;
    private TextView yearSumText;
    private TextView monthSumText;
    private RelativeLayout settleLayout;
    private RelativeLayout unpaidLayout;
    private TextView statisticsText;
    private Spinner monthsSpinner;
    //数据
    private List<SettleBill> allSettleBillList = new ArrayList<>();
    private List<SettleBill> yearBillList = new ArrayList<>();
    private List<SettleBill> monthBillList = new ArrayList<>();
    private final String[] types = {"生活用品", "学习用品", "娱乐消费", "食物消费", "借出", "其他"};
    private String yearString; //保存选择的年份
    private String monthString;
    private float[] monthlyConsumption = new float[12];  //每月消费总额
    private float[] daylyConsumption = new float[31];   //每日
    private float monthlyAverageConsumption = 0;
    private float daylyAverageConsumption = 0;
    private float lifeConsumption = 0;
    private float studyConsumption = 0;
    private float amusementConsumption = 0;
    private float foodConsumption = 0;
    private float loanConsumption = 0;
    private float othersConsumption = 0;
    private float[] consumptions = new float[6];
    private float maxConsumption = 0;
    // private int maxMark = 0;//0代表生活用品为最大值，以此类推
    List<Entry> yearEntries = new ArrayList<>();
    List<Entry> monthEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);//加入集合
        setContentView(R.layout.activity_statistics);
        iniWidget();
        navigationView.defaultSetting();
        navigationView.setContext(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        title_text.setText("统计");
        title_text.setTextColor(Color.BLACK);
        statisticsText.setTextColor(Color.BLUE);
        iniTimeString();
        setAdapter();
        loadAllBillList();
        setListener();
        setDefaultTimeData();//设置默认年份及月份
        setYearLineChart();
        setMonthLineChart();
        setRadarChart();
    }

    private void setDefaultTimeData() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+8:00"));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        yearSpinner.setSelection(getYearPosition(year));
        monthsSpinner.setSelection(getMonthPosition(month) + 1);
    }

    private int getMonthPosition(String month) {
        Log.w("addBill", "month:" + month);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(month))
                return i;
        }
        return 0;
    }

    private int getYearPosition(String year) {
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals(year))
                return i;
        }
        return 0;
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

    private void setRadarChart() {
        radarChart.getDescription().setEnabled(false);//不设置描述
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(200);//网格透明度
        radarChart.setRotationEnabled(true);
        setData();
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(types));
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(1, false);
        yAxis.setTextSize(12f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);
    }

    private void setData() {//设置数据
        ArrayList<RadarEntry> entries = new ArrayList<>();
        float miniData = 10;
        entries.add(new RadarEntry(lifeConsumption / maxConsumption * 100 + miniData));
        entries.add(new RadarEntry(studyConsumption / maxConsumption * 100 + miniData));
        entries.add(new RadarEntry(amusementConsumption / maxConsumption * 100 + miniData));
        entries.add(new RadarEntry(foodConsumption / maxConsumption * 100 + miniData));
        entries.add(new RadarEntry(loanConsumption / maxConsumption * 100 + miniData));
        entries.add(new RadarEntry(othersConsumption / maxConsumption * 100 + miniData));
        RadarDataSet set = new RadarDataSet(entries, "账单类型");
        set.setDrawFilled(true);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);
        RadarData data = new RadarData(set);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        radarChart.setData(data);
        radarChart.invalidate();
    }

    private void setMonthLineChart() {
        List<String> xList = new ArrayList<>();
        Description description = new Description();
        description.setText("月消费表");
        description.setTextColor(Color.BLUE);
        monthLineChart.setDescription(description);
        monthLineChart.setPinchZoom(true);   //可以同步放大X轴Y轴
        //x轴操作

        XAxis xAxis = monthLineChart.getXAxis();
        for (int i = 0; i < 31; i++) {
            xList.add(String.valueOf(i + 1).concat("日"));
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xList));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xList));
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//显示在底部
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxisRight = monthLineChart.getAxisRight();
        yAxisRight.setEnabled(false);//不显示右侧Y轴
        //设置数据
        loadMonthEntries();
        loadMonthLineChart();
    }

    private void setYearLineChart() {
        List<String> xList = new ArrayList<>();
        Description description = new Description();
        description.setText("年度消费表");
        description.setTextColor(Color.BLUE);
        yearLineChart.setDescription(description);
        yearLineChart.setPinchZoom(true);   //可以同步放大X轴Y轴
        //x轴操作

        XAxis xAxis = yearLineChart.getXAxis();
        for (int i = 0; i < 12; i++) {
            xList.add(String.valueOf(i + 1).concat("月"));
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xList));
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//显示在底部
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxisRight = yearLineChart.getAxisRight();
        yAxisRight.setEnabled(false);//不显示右侧Y轴
        //设置数据
        loadYearEntries();
        loadYearLineChart();
    }

    private void loadYearEntries() {
        yearEntries.clear();
        for (int i = 0; i < 12; i++) {
            yearEntries.add(new Entry(i, monthlyConsumption[i]));
        }
    }

    private void loadYearLineChart() {
        LineDataSet lineDataSet = new LineDataSet(yearEntries, "月消费");
        LineData data = new LineData(lineDataSet);
        LimitLine limitLine = new LimitLine(monthlyAverageConsumption, "月平均消费：" + monthlyAverageConsumption);
        limitLine.setLineColor(Color.RED);
        YAxis yAxisLeft = yearLineChart.getAxisLeft();
        yAxisLeft.removeAllLimitLines();//移除之前所有的限制线
        yAxisLeft.addLimitLine(limitLine);
        yAxisLeft.setLabelCount(5);
        yearLineChart.setData(data);
        yearLineChart.notifyDataSetChanged();
        yearLineChart.invalidate();
    }

    private void setListener() {
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearString = yearSpinner.getSelectedItem().toString();
                loadYearSettleBill(yearString);
                countMonthlyConsumption();
                loadYearLineChart();
                //年消费表改变后 月消费表改为对应年份的
                monthString = monthsSpinner.getSelectedItem().toString();
                loadMonthSettleBill(monthString);
                countDaylyConsumption();
                loadMonthLineChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        monthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthString = monthsSpinner.getSelectedItem().toString();
                loadMonthSettleBill(monthString);
                countDaylyConsumption();
                loadMonthLineChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        settleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticsActivity.this, SettleActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        unpaidLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticsActivity.this, UnpaidActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void loadMonthLineChart() {
        LineDataSet lineDataSet = new LineDataSet(monthEntries, "日消费");
        LineData data = new LineData(lineDataSet);
        LimitLine limitLine = new LimitLine(daylyAverageConsumption, "日平均消费：" + daylyAverageConsumption);
        limitLine.setLineColor(Color.RED);
        YAxis yAxisLeft = monthLineChart.getAxisLeft();
        yAxisLeft.removeAllLimitLines();//移除之前所有的限制线
        yAxisLeft.addLimitLine(limitLine);
        yAxisLeft.setLabelCount(5);
        monthLineChart.setData(data);
        monthLineChart.notifyDataSetChanged();
        monthLineChart.invalidate();
    }

    private void loadMonthSettleBill(String month) {
        Log.w("statistics", month);
        double sum = 0;
        monthBillList.clear();
        for (SettleBill settleBill : yearBillList) {
            if (getMonth(settleBill.getCreateTime()).equals(month)) {
                monthBillList.add(settleBill);
                sum = sum + settleBill.getTotal();
                Log.w("Statics", settleBill.toString());
            }
        }
        monthSumText.setText("月总消费:" + sum);
        if (month.equals("1") || month.equals("3") || month.equals("5") || month.equals("7") || month.equals("8") || month.equals("10") || month.equals("12"))
            daylyAverageConsumption = (float) sum / 31;
        else if (month.equals("2"))
            daylyAverageConsumption = (float) sum / 29;
        else daylyAverageConsumption = (float) sum / 30;
    }

    private void countDaylyConsumption() {
        for (int i = 0; i < 12; i++) { //初始化
            daylyConsumption[i] = 0;
        }
        for (int i = 0; i < 31; i++) {
            for (SettleBill settleBill : monthBillList) {
                if (getDay(settleBill.getCreateTime()).equals(i + 1 + "")) {
                    daylyConsumption[i] = daylyConsumption[i] + (float) settleBill.getTotal();
                }
            }
            Log.w("statistics", i + 1 + "日消费:" + daylyConsumption[i]);
        }
        loadMonthEntries(); //更新月消费数据
    }

    private String getDay(String createTime) {
        String day = "";
        int mark = 0;
        char[] array = createTime.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] != '/') {
                if (mark == 2)
                    day= day + array[i];
            } else
                mark++;
        }
        return day;
    }

    private void loadMonthEntries() {
        monthEntries.clear();
        for (int i = 0; i < 31; i++) {
            monthEntries.add(new Entry(i, daylyConsumption[i]));
            //   yearEntries.add(new Entry(i, (float) (Math.random()) * 80));
            Log.w("load", i + 1 + "日消费：" + daylyConsumption[i]);
        }
    }

    private void countMonthlyConsumption() {
        for (int i = 0; i < 12; i++) { //初始化
            monthlyConsumption[i] = 0;
        }
        for (int i = 0; i < 12; i++) {
            for (SettleBill settleBill : yearBillList) {
                if (getMonth(settleBill.getCreateTime()).equals(i + 1 + "")) {
                    monthlyConsumption[i] = monthlyConsumption[i] + (float) settleBill.getTotal();
                }
            }
            Log.w("statistics", i + 1 + "月消费:" + monthlyConsumption[i]);
        }
        loadYearEntries(); //更新年消费数据
    }

    private void loadAllBillList() {
        allSettleBillList = DataSupport.where("username='" + MyAcount.userName + "'").find(SettleBill.class);
        if (allSettleBillList.size() > 0) {
            for (SettleBill settleBill : allSettleBillList) {
                if (settleBill.getType().equals("生活用品")) {
                    lifeConsumption = lifeConsumption + (float) settleBill.getTotal();
                } else if (settleBill.getType().equals("学习用品")) {
                    studyConsumption = studyConsumption + (float) settleBill.getTotal();
                } else if (settleBill.getType().equals("娱乐消费")) {
                    amusementConsumption = amusementConsumption + (float) settleBill.getTotal();
                } else if (settleBill.getType().equals("食物消费")) {
                    foodConsumption = foodConsumption + (float) settleBill.getTotal();
                } else if (settleBill.getType().equals("借出")) {
                    loanConsumption = loanConsumption + (float) settleBill.getTotal();
                } else if (settleBill.getType().equals("其他")) {
                    othersConsumption = othersConsumption + (float) settleBill.getTotal();
                }
                consumptions[0] = lifeConsumption;
                consumptions[1] = studyConsumption;
                consumptions[2] = amusementConsumption;
                consumptions[3] = foodConsumption;
                consumptions[4] = loanConsumption;
                consumptions[5] = othersConsumption;
                maxConsumption = getMaxConsumption();
            }
        }
    }

    private float getMaxConsumption() {
        float max = lifeConsumption;
        for (int i = 0; i < 6; i++) {
            if (consumptions[i] > max) {
                max = consumptions[i];
            }
        }
        return max;
    }

    private void loadYearSettleBill(String year) {
        Log.w("statistics", year);
        double sum = 0;
        yearBillList.clear();
        for (SettleBill settleBill : allSettleBillList) {
            if (getYear(settleBill.getCreateTime()).equals(year)) {
                yearBillList.add(settleBill);
                sum = sum + settleBill.getTotal();
                Log.w("Statics", settleBill.toString());
            }
        }
        yearSumText.setText("年度总消费:" + sum);
        monthlyAverageConsumption = (float) sum / 12;
    }

    private String getYear(String createTime) {
        String year = "";
        char[] array = createTime.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '/') {
                return year;
            } else year = year + array[i];
        }
        return year;
    }

    private String getMonth(String createTime) {
        String month = "";
        int mark = 0;
        char[] array = createTime.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] != '/') {
                if (mark == 1)
                    month = month + array[i];
                if (mark > 1) {
                    return month;
                }
            } else
                mark++;
        }
        return month;
    }

    public void iniTimeString() {
        //初始化年 1990~2090年
        for (int i = 0; i <= 100; i++) {
            years[i] = (i + 1990) + "";
        }
        //初始化月1~12月
        for (int i = 0; i < 12; i++) {
            months[i] = (i + 1) + "";
        }
    }

    private void setAdapter() {
        yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        yearSpinner.setAdapter(yearsAdapter);
        monthsSpinner.setAdapter(monthsAdapter);
    }

    private void iniWidget() {
        toolbar = findViewById(R.id.toolbar);
        title_text = findViewById(R.id.title_text);
        yearSpinner = findViewById(R.id.statistics_year_spinner);
        monthsSpinner = findViewById(R.id.statistics_month_spinner);
        yearLineChart = findViewById(R.id.statistics_year_lineChart);
        monthLineChart = findViewById(R.id.statistics_month_lineChart);
        yearSumText = findViewById(R.id.statistics_yearSum_text);
        monthSumText = findViewById(R.id.statistics_monthSum_text);
        settleLayout = findViewById(R.id.settle_layout);
        unpaidLayout = findViewById(R.id.unpaid_layout);
        statisticsText = findViewById(R.id.statistics_text);
        radarChart = findViewById(R.id.statistics_radar);
        drawerLayout = findViewById(R.id.statistics_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
