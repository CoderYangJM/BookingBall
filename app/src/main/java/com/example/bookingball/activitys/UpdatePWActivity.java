package com.example.bookingball.activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingball.ActivityManager;
import com.example.bookingball.MyAcount;
import com.example.bookingball.R;
import com.example.bookingball.database.UserAccountPW;

import org.litepal.crud.DataSupport;

public class UpdatePWActivity extends AppCompatActivity {

    private EditText oldPW;
    private EditText newPW;
    private EditText checkPW;
    private Toolbar toolbar;
    private TextView oldPWYes;
    private TextView oldPWNo;
    private TextView newPWYes;
    private TextView newPWNo;
    private TextView checkPWYes;
    private TextView checkPWNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pw);
        ActivityManager.addActivity(this);
        iniWidget();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        hideYesOrNo();
        addListener();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        //隐藏另外两个menu,将对应menu显示
        MenuItem addNewBillItem = menu.findItem(R.id.addNewBills);
        addNewBillItem.setVisible(false);
        MenuItem mulItem = menu.findItem(R.id.multiple_choice);
        mulItem.setVisible(false);
        MenuItem saveItem = menu.findItem(R.id.save_data);
        saveItem.setVisible(true);
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
        if (oldPWYes.getVisibility() == View.VISIBLE && newPWYes.getVisibility() == View.VISIBLE && checkPWYes.getVisibility() == View.VISIBLE) {
            ContentValues values = new ContentValues();
            values.put("password", newPW.getText().toString());
            DataSupport.update(UserAccountPW.class, values, MyAcount.id);
            Toast.makeText(UpdatePWActivity.this, "修改成功,请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdatePWActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityManager.removeAll();
        } else {
            Toast.makeText(UpdatePWActivity.this, "请检查密码是否都输入正确", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideYesOrNo() {
        oldPWYes.setVisibility(View.GONE);
        newPWYes.setVisibility(View.GONE);
        checkPWYes.setVisibility(View.GONE);
        checkPWNo.setVisibility(View.GONE);
        newPWNo.setVisibility(View.GONE);
        checkPWNo.setVisibility(View.GONE);
    }

    private void addListener() {
        oldPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (oldPW.getText().toString().equals(MyAcount.passWord)) {
                    oldPWYes.setVisibility(View.VISIBLE);
                    oldPWNo.setVisibility(View.GONE);
                } else {
                    oldPWYes.setVisibility(View.GONE);
                    oldPWNo.setVisibility(View.VISIBLE);
                }
            }
        });
        newPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkPWFormat()) {
                    newPWNo.setVisibility(View.GONE);
                    newPWYes.setVisibility(View.VISIBLE);
                } else {
                    newPWNo.setVisibility(View.VISIBLE);
                    newPWYes.setVisibility(View.GONE);
                }
            }
        });
        checkPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (newPW.getText().toString().equals(checkPW.getText().toString())) {
                    checkPWNo.setVisibility(View.GONE);
                    checkPWYes.setVisibility(View.VISIBLE);
                } else {
                    checkPWNo.setVisibility(View.VISIBLE);
                    checkPWYes.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean checkPWFormat() {
        if (newPW.getText().toString().length() < 6 || newPW.getText().toString().length() > 16) {
            if (newPW.getText().toString().length() > 16)
                Toast.makeText(this, "密码应在6~16位之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void iniWidget() {
        oldPW = findViewById(R.id.update_oldPW);
        newPW = findViewById(R.id.update_newPW);
        checkPW = findViewById(R.id.update_checkNewPW);
        oldPWYes = findViewById(R.id.update_oldPW_yes);
        oldPWNo = findViewById(R.id.update_oldPW_no);
        newPWYes = findViewById(R.id.update_newPW_yes);
        newPWNo = findViewById(R.id.update_newPW_no);
        checkPWYes = findViewById(R.id.update_checkNewPW_yes);
        checkPWNo = findViewById(R.id.update_checkNewPW_no);
        toolbar = findViewById(R.id.update_toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
