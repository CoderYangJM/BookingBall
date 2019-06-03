package com.example.bookingball.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingball.ActivityManager;
import com.example.bookingball.MyAcount;
import com.example.bookingball.R;
import com.example.bookingball.database.SettleBill;
import com.example.bookingball.database.UnpaidBill;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBillActivity extends AppCompatActivity {
    private int updateBillType = 0; //0,表示新建账单，1表示修改未结账单，2表示修改已结账单
    private UnpaidBill unpaidBill;  //用来保存未结账目引用
    private SettleBill settleBill;
    private LinearLayout returnedLayout;
    private LinearLayout limitDateLayout;
    private LinearLayout billTypeLayout;
    private TextView idText;
    private Toolbar toolbar;
    private TextView title_txt;
    private EditText returnedEdit;
    private EditText nameEdit;
    private EditText remarkEdit;
    private EditText numberEdit;
    private EditText telEdit;
    private Spinner limitYear;
    private String limitYearString = "";
    private Spinner limitMonth;
    private String limitMonthString = "";
    private Spinner limitDay;
    private String limitDayString = "";
    private Spinner createYear;
    private String createYearString = "";
    private Spinner createMonth;
    private String createMonthString = "";
    private Spinner createDay;
    private String createDayString = "";
    private Spinner type;
    private String typeString = "";
    private CheckBox checkBox;
    private Button useNowTime;
    private Spinner billType;
    private final String[] years = new String[101];
    private final String[] months = new String[12];
    private final String[] days = new String[31];
    private final String[] types = {"生活用品", "学习用品", "娱乐消费", "食物消费", "借出", "其他"};
    private final String[] billTypes = {"已结账单", "未结账单"};
    private ArrayAdapter<String> billTypeAdapter;
    private ArrayAdapter<String> typesAdapter;
    private ArrayAdapter<String> yearsAdapter;
    private ArrayAdapter<String> monthsAdapter;
    private ArrayAdapter<String> daysAdapter;
    private String telPat = "(([0-9]{3,4}-)?[0-9]+)?";  //可以不填写电话
    private Pattern telPattern = Pattern.compile(telPat);
    private String numberPat = "[0-9]+(\\.[0-9]{1,2})?"; //整数或者一到两位小数
    private Pattern numberPattern = Pattern.compile(numberPat);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);//加入集合
        setContentView(R.layout.activity_add_bill);
        iniWidget();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent.getSerializableExtra("settleBill") != null) {
            title_txt.setText("修改已结账单");
            limitDateLayout.setVisibility(View.GONE);
            returnedLayout.setVisibility(View.GONE);
            billTypeLayout.setVisibility(View.GONE);
            updateBillType = 2;
        } else if (intent.getSerializableExtra("unpaidBill") != null) {
            title_txt.setText("修改未结账单");
            billTypeLayout.setVisibility(View.GONE);
            updateBillType = 1;
        } else {
            title_txt.setText("新建账单");
            returnedLayout.setVisibility(View.GONE);
            idText.setVisibility(View.GONE);
            //设置选定不设置限款时间
            checkBox.setChecked(true);
        }
        title_txt.setTextColor(Color.BLACK);
        //初始化时间数组
        iniTimeString();
        //设置Spinner适配器
        setAdapter();
        if (updateBillType == 1) //如果当前模式为修改未结账单
        {
            unpaidBill = (UnpaidBill) intent.getSerializableExtra("unpaidBill");
            setWidgetAsUnpaidBill(unpaidBill);
        }
        if (updateBillType == 2) //如果当前模式为修改已结账单
        {
            settleBill = (SettleBill) intent.getSerializableExtra("settleBill");
            setWidgetAsSettleBill(settleBill);
        }
        //设置spinner监听器；
        setListener();
    }

    private void setWidgetAsUnpaidBill(UnpaidBill unpaidBill) {
        idText.setText("未结账单编号:" + unpaidBill.getId());
        nameEdit.setText(unpaidBill.getName());
        numberEdit.setText(unpaidBill.getTotal() + "");
        remarkEdit.setText(unpaidBill.getRemarks());
        telEdit.setText(unpaidBill.getTel());
        returnedEdit.setText(unpaidBill.getReturned() + "");
        type.setSelection(getTypePosition(unpaidBill.getType()));
        splitCreateDate(unpaidBill.getCreateDate());
        createYear.setSelection(getYearPosition(createYearString));
        createMonth.setSelection(getMonthPosition(createMonthString));
        createDay.setSelection(getDayPosition(createDayString));
        if (unpaidBill.getLimitDate() != null && !unpaidBill.getLimitDate().equals("")) {
            splitLimitDate(unpaidBill.getLimitDate());
            limitYear.setSelection(getYearPosition(limitYearString));
            limitMonth.setSelection(getMonthPosition(limitMonthString));
            limitDay.setSelection(getDayPosition(limitDayString));
        } else {
            checkBox.setChecked(true);
        }
    }

    private void setWidgetAsSettleBill(SettleBill settleBill) {
        idText.setText("已结账单编号:" + settleBill.getId());
        nameEdit.setText(settleBill.getName());
        numberEdit.setText(settleBill.getTotal() + "");
        remarkEdit.setText(settleBill.getRemarks());
        telEdit.setText(settleBill.getTel());
        type.setSelection(getTypePosition(settleBill.getType()));
        splitCreateDate(settleBill.getCreateTime());
        createYear.setSelection(getYearPosition(createYearString));
        createMonth.setSelection(getMonthPosition(createMonthString));
        createDay.setSelection(getDayPosition(createDayString));
    }

    private void splitLimitDate(String dateString) {
        int remark = 0;
        Log.w("addBill", "dateString:" + dateString);
        char[] dataArray = dateString.toCharArray();
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i] != '/') {
                if (remark == 0) {
                    limitYearString = limitYearString + dataArray[i];
                } else if (remark == 1) {
                    limitMonthString = limitMonthString + dataArray[i];
                } else if (remark == 2) {
                    limitDayString = limitDayString + dataArray[i];
                }
            } else remark++;
        }
    }

    private void splitCreateDate(String dateString) {
        int remark = 0;
        char[] dataArray = dateString.toCharArray();
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i] != '/') {
                if (remark == 0) {
                    createYearString = createYearString + dataArray[i];
                } else if (remark == 1) {
                    createMonthString = createMonthString + dataArray[i];
                } else if (remark == 2) {
                    createDayString = createDayString + dataArray[i];
                }
            } else remark++;
        }
        Log.w("addBill", "year:" + createYearString + ",month:" + createMonthString + ",day:" + createDayString);
    }

    private void setListener() {
        limitYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                limitYearString = limitYear.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        limitMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                limitMonthString = limitDay.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        limitDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                limitDayString = limitDay.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createYearString = createYear.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createMonthString = createMonth.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createDayString = createDay.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeString = type.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //ButtonListener
        useNowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance(TimeZone.getTimeZone("UTC+8:00"));//获取calendar实例并设置时区
                String year = String.valueOf(time.get(Calendar.YEAR));
                String month = String.valueOf(time.get(Calendar.MONTH));
                String day = String.valueOf(time.get(Calendar.DATE));
                createYear.setSelection(getYearPosition(year));
                createMonth.setSelection(getMonthPosition(month) + 1);
                createDay.setSelection(getDayPosition(day));
            }
        });
    }

    private int getYearPosition(String year) {
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals(year))
                return i;
        }
        return 0;
    }

    private int getMonthPosition(String month) {
        Log.w("addBill", "month:" + month);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(month))
                return i;
        }
        return 0;
    }

    private int getDayPosition(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(day))
                return i;
        }
        return 0;
    }

    private int getTypePosition(String type) {
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type))
                return i;
        }
        return 0;
    }

    private boolean checkTelFormat(String tel) {
        Matcher matcher = telPattern.matcher(tel);
        return matcher.matches();
    }

    private boolean checkNumberFormat(String number) {
        Matcher matcher = numberPattern.matcher(number);
        return matcher.matches();
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
        //初始化天1~31
        for (int i = 0; i < 31; i++) {
            days[i] = (i + 1) + "";
        }
    }

    private void setAdapter() {
        yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        typesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        billTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, billTypes);
        type.setAdapter(typesAdapter);
        createYear.setAdapter(yearsAdapter);
        createMonth.setAdapter(monthsAdapter);
        createDay.setAdapter(daysAdapter);
        limitYear.setAdapter(yearsAdapter);
        limitMonth.setAdapter(monthsAdapter);
        limitDay.setAdapter(daysAdapter);
        billType.setAdapter(billTypeAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        //隐藏另外两个menu,将对应menu显示
        MenuItem addNewBillItem = menu.findItem(R.id.addNewBills);
        addNewBillItem.setVisible(false);
        MenuItem saveItem = menu.findItem(R.id.save_data);
        saveItem.setVisible(true);
        MenuItem mulItem = menu.findItem(R.id.multiple_choice);
        mulItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_data:
                saveData();
                break;
            default:
                break;
        }
        return true;
    }

    private void saveData() {

        Log.w("AddBill", billType.getSelectedItem().toString());
        if (updateBillType == 0) {//新建账单保存
            if (billType.getSelectedItem().toString().equals("未结账单")) {  //保存未结账单
                if (checkFormat()) {
                    UnpaidBill unpaidBill = new UnpaidBill();
                    unpaidBill.setUserName(MyAcount.userName);
                    unpaidBill.setCreateDate(createYearString + "/" + createMonthString + "/" + createDayString);
                    if (!checkBox.isChecked())
                        unpaidBill.setLimitDate(limitYearString + "/" + limitMonthString + "/" + limitDayString);
                    unpaidBill.setName(nameEdit.getText().toString());
                    unpaidBill.setTotal(Double.parseDouble(this.numberEdit.getText().toString()));
                    unpaidBill.setRemarks(remarkEdit.getText().toString());
                    unpaidBill.setTel(telEdit.getText().toString());
                    unpaidBill.setType(typeString);
                    unpaidBill.save();
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    Log.w("addBill", unpaidBill.toString());
                    Intent intent = new Intent(this, UnpaidActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (billType.getSelectedItem().toString().equals("已结账单")) {//保存已结账单
                if (checkFormat()) {
                    SettleBill settleBill = new SettleBill();
                    settleBill.setUserName(MyAcount.userName);
                    settleBill.setCreateTime(createYearString + "/" + createMonthString + "/" + createDayString);
                    settleBill.setName(nameEdit.getText().toString());
                    settleBill.setTotal(Double.parseDouble(this.numberEdit.getText().toString()));
                    settleBill.setRemarks(remarkEdit.getText().toString());
                    settleBill.setTel(telEdit.getText().toString());
                    settleBill.setType(typeString);
                    settleBill.save();
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    Log.w("addBill", settleBill.toString());
                    Intent intent = new Intent(this, SettleActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else if (updateBillType == 2) {//修改已结账单
            settleBill.setCreateTime(createYearString + "/" + createMonthString + "/" + createDayString);
            settleBill.setName(nameEdit.getText().toString());
            settleBill.setTotal(Double.parseDouble(this.numberEdit.getText().toString()));
            settleBill.setRemarks(remarkEdit.getText().toString());
            settleBill.setTel(telEdit.getText().toString());
            settleBill.setType(typeString);
            settleBill.update(settleBill.getId());
            Intent intent = new Intent(this, SettleActivity.class);
            startActivity(intent);
            finish();
        } else if (updateBillType == 1) {//修改未结账单
            unpaidBill.setCreateDate(createYearString + "/" + createMonthString + "/" + createDayString);
            if (!checkBox.isChecked())
                unpaidBill.setLimitDate(limitYearString + "/" + limitMonthString + "/" + limitDayString);
            else unpaidBill.setLimitDate("");
            unpaidBill.setName(nameEdit.getText().toString());
            unpaidBill.setTotal(Double.parseDouble(this.numberEdit.getText().toString()));
            unpaidBill.setRemarks(remarkEdit.getText().toString());
            unpaidBill.setTel(telEdit.getText().toString());
            unpaidBill.setType(typeString);
            unpaidBill.setReturned(Double.parseDouble(returnedEdit.getText().toString()));
            unpaidBill.update(unpaidBill.getId());
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UnpaidActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkFormat() {
        //不填写姓名和金额
        if (nameEdit.getText() == null || nameEdit.getText().toString() == "") {
            Toast.makeText(AddBillActivity.this, "请填写对方性名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (numberEdit.getText() == null || numberEdit.getText().toString() == "") {
            Toast.makeText(AddBillActivity.this, "请填写金额", Toast.LENGTH_SHORT).show();
            return false;
        }
        //数字及电话不合法
        if (!checkNumberFormat(numberEdit.getText().toString())) {
            Toast.makeText(AddBillActivity.this, "金额只能是整数或包含两位以内小数", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkTelFormat(telEdit.getText().toString())) {
            Toast.makeText(AddBillActivity.this, "请输入正确格式的电话号码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void iniWidget() {
        toolbar = findViewById(R.id.toolbar);
        title_txt = findViewById(R.id.title_text);
        nameEdit = findViewById(R.id.add_name_edit);
        telEdit = findViewById(R.id.add_tel_edit);
        remarkEdit = findViewById(R.id.add_remark_edit);
        numberEdit = findViewById(R.id.add_number_edit);
        createYear = findViewById(R.id.add_year_spinner);
        createMonth = findViewById(R.id.add_month_spinner);
        createDay = findViewById(R.id.add_day_spinner);
        limitYear = findViewById(R.id.add_limit_year_spinner);
        limitMonth = findViewById(R.id.add_limit_month_spinner);
        limitDay = findViewById(R.id.add_limit_day_spinner);
        type = findViewById(R.id.add_type_spinner);
        checkBox = findViewById(R.id.add_checkbox);
        useNowTime = findViewById(R.id.add_use_localtime);
        billType = findViewById(R.id.add_billType_spinner);
        limitDateLayout = findViewById(R.id.add_limitDate_layout);
        returnedLayout = findViewById(R.id.add_returned_layout);
        returnedEdit = findViewById(R.id.add_returned_edit);
        idText = findViewById(R.id.add_id_text);
        billTypeLayout = findViewById(R.id.add_billType_layout);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

}
