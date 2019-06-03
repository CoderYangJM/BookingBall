package com.example.bookingball.activitys;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingball.ActivityManager;
import com.example.bookingball.R;
import com.example.bookingball.database.UserAccountPW;

import org.litepal.crud.DataSupport;

public class RegisterActivity extends AppCompatActivity {
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText checkPWEdit;
    private TextView accountYes;
    private TextView pwYes;
    private TextView pwCheckYes;
    private TextView accountNO;
    private TextView pwNO;
    private TextView pwCheckNO;
    private Button registerButton;
    private TextWatcher userNameEditListener;
    private TextWatcher passwordEditListener;
    private TextWatcher checkPWEditListener;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);//加入集合
        setContentView(R.layout.activity_register);
        iniWidget();
        passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        checkPWEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        hideYesAndNo();//隐藏记号
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.shape_button);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        instanceListener();
        userNameEdit.addTextChangedListener(userNameEditListener); //为用户名Edit添加监听器
        passwordEdit.addTextChangedListener(passwordEditListener);
        checkPWEdit.addTextChangedListener(checkPWEditListener);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查用户名是否存在
                if (!checkUserNameExist()) {
                    if (accountYes.getVisibility() == View.VISIBLE && pwCheckYes.getVisibility() == View.VISIBLE) {
                        UserAccountPW user = new UserAccountPW();
                        user.setUserName(userNameEdit.getText().toString());
                        user.setPassword(passwordEdit.getText().toString());
                        user.save();
                        //postToServer();//提交给服务器保存账号信息
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "请检查密码是否输入合法", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void instanceListener() {
        userNameEditListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkUserNameFormat()) {
                    accountYes.setVisibility(View.VISIBLE);
                    accountNO.setVisibility(View.GONE);
                } else {
                    accountYes.setVisibility(View.GONE);
                    accountNO.setVisibility(View.VISIBLE);
                }
            }
        };
        passwordEditListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkPWFormat()) {
                    pwNO.setVisibility(View.GONE);
                    pwYes.setVisibility(View.VISIBLE);
                } else {
                    pwNO.setVisibility(View.VISIBLE);
                    pwYes.setVisibility(View.GONE);
                }
            }
        };
        checkPWEditListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordEdit.getText().toString().equals(checkPWEdit.getText().toString())) {
                    pwCheckNO.setVisibility(View.GONE);
                    pwCheckYes.setVisibility(View.VISIBLE);
                } else {
                    pwCheckNO.setVisibility(View.VISIBLE);
                    pwCheckYes.setVisibility(View.GONE);
                }
            }
        };
    }

    private void hideYesAndNo() {
        accountYes.setVisibility(View.GONE);
        pwYes.setVisibility(View.GONE);
        pwCheckYes.setVisibility(View.GONE);
        accountNO.setVisibility(View.GONE);
        pwNO.setVisibility(View.GONE);
        pwCheckNO.setVisibility(View.GONE);
    }

    private void iniWidget() {
        userNameEdit = findViewById(R.id.register_userName);
        passwordEdit = findViewById(R.id.register_password);
        checkPWEdit = findViewById(R.id.register_check);
        registerButton = findViewById(R.id.register_button);
        accountYes = findViewById(R.id.register_account_yes);
        pwYes = findViewById(R.id.register_pw_yes);
        pwCheckYes = findViewById(R.id.register_check_yes);
        accountNO = findViewById(R.id.register_account_no);
        pwNO = findViewById(R.id.register_pw_no);
        pwCheckNO = findViewById(R.id.register_check_no);
        toolbar = findViewById(R.id.toolbar);
    }


    private boolean checkUserNameFormat() {
        if (userNameEdit.getText().toString().length() > 12) {
            Toast.makeText(this, "用户名不得超过12字符", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkUserNameExist() {
        //从服务器查询用户名是否存在
        if (DataSupport.isExist(UserAccountPW.class, "username='" + userNameEdit.getText().toString() + "'"))
            return true;
        return false;
    }

    private boolean checkPWFormat() {
        if (passwordEdit.getText().toString().length() < 6 || passwordEdit.getText().toString().length() > 16) {
            if (passwordEdit.getText().toString().length() > 16)
                Toast.makeText(this, "密码应在6~16位之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
