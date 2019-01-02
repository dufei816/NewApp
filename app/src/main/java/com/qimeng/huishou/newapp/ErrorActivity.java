package com.qimeng.huishou.newapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.qimeng.huishou.newapp.util.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class ErrorActivity extends AppCompatActivity {


    @BindView(R.id.tv_error)
    TextView tvError;

    private CaocConfig config;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);
        initData();
        if (!Config.DEBUG) {
            CustomActivityOnCrash.restartApplication(this, config);
        }
    }

    private void initData() {
        //将堆栈跟踪作为字符串获取。
        String stackString = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());
        Log.d("huangxiaoguo", "将堆栈跟踪作为字符串获取==>" + stackString);
        tvError.setText(stackString + "\n");
        //获取错误报告的Log信息
        String logString = CustomActivityOnCrash.getActivityLogFromIntent(getIntent());
        Log.d("huangxiaoguo", "获取错误报告的Log信息==>" + logString);
        tvError.append(logString + "\n");
        // 获取所有的信息
        String allString = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());
        Log.d("huangxiaoguo", "获取所有的信息==>" + allString);
        tvError.append(allString + "\n");
        //获得配置信息,比如设置的程序崩溃显示的页面和重新启动显示的页面等等信息
        config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
    }
}