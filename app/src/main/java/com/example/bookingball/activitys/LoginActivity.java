package com.example.bookingball.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookingball.ActivityManager;
import com.example.bookingball.MyAcount;
import com.example.bookingball.R;

import org.litepal.crud.DataSupport;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText accountEdit;
    private EditText passWordEdit;
    private Button loginButton;
    private CheckBox rememberBox;
    private Button forgetButton;
    private Button registerButton;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);//加入集合
        setContentView(R.layout.activity_login);
        iniWidget();
        passWordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        //获取共享优先数据流
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isRemember = preferences.getBoolean("remember_pw", false);
        //读取布尔值用以判断用户上次登录是否选择了记住密码
        if (isRemember == true) {
            accountEdit.setText(preferences.getString("account", ""));
            passWordEdit.setText(preferences.getString("password", ""));
            rememberBox.setChecked(true);
            //加载数据
        }

        loginButton.setOnClickListener(this);
        forgetButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                login();
                break;
            case R.id.forget:
                break;
            case R.id.register:
                //跳转注册界面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void loadAccountPWByName(String userName) {
        Cursor cursor = DataSupport.findBySQL("select * from useraccountpw where username='" + userName + "';");
        if (cursor != null && cursor.moveToFirst()) {
            passWord = cursor.getString(cursor.getColumnIndex("password"));
            MyAcount.id = cursor.getInt(cursor.getColumnIndex("id"));
            Log.w("login", passWord);
        } else {
            Toast.makeText(this, "暂无此账号", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        String account = accountEdit.getText().toString();
        String password = passWordEdit.getText().toString();
        loadAccountPWByName(accountEdit.getText().toString());//从本地数据库加载用户密码
        if (passWord != null) {
            if (password.equals(passWord)) {
                editor = preferences.edit();
                if (rememberBox.isChecked()) {//判断“记住密码”选项是否被勾选
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.putBoolean("remember_pw", true);
                } else editor.clear();
                editor.apply();  //提交(保存)数据
                MyAcount.userName = accountEdit.getText().toString();
                MyAcount.passWord = passWordEdit.getText().toString();
                Intent intent = new Intent(LoginActivity.this, UnpaidActivity.class);
                startActivity(intent);
                finish();//结束当前活动
            } else {
                Toast.makeText(this, "密码或用户名错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "暂无此账户！", Toast.LENGTH_SHORT).show();
        }
    }

    public void iniWidget() {
        accountEdit = findViewById(R.id.account);
        passWordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        rememberBox = findViewById(R.id.remember_pw);
        forgetButton = findViewById(R.id.forget);
        registerButton = findViewById(R.id.register);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
