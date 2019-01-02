package com.qimeng.huishou.newapp.util;

import android.app.Application;
import android.content.Context;

import com.igexin.sdk.PushManager;
import com.qimeng.huishou.newapp.DemoIntentService;
import com.qimeng.huishou.newapp.ErrorActivity;
import com.qimeng.huishou.newapp.MainActivity;
import com.qimeng.huishou.newapp.net.HttpUtil;

import cat.ereza.customactivityoncrash.config.CaocConfig;

public class App extends Application {

    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面，
        //BackgroundMode.BACKGROUND_MODE_CRASH:      //当应用程序处于后台崩溃时显示默认系统错误（一个系统提示的错误对话框），
        //BackgroundMode.BACKGROUND_MODE_SILENT:     //当应用程序处于后台时崩溃，默默地关闭程序！这种模式我感觉最好
        CaocConfig.Builder.create()
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
//                .enabled(true) //default: true这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
//                .showErrorDetails(true) //default: true 这将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪。
//                .showRestartButton(false) //default: true
//                .logErrorOnRestart(false) //default: true
//                .trackActivities(true) //default: false
//                .minTimeBetweenCrashesMs(2000) //default: 3000
//                .errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
                .restartActivity(MainActivity.class)//default: null (your app's launch activity)
                .errorActivity(ErrorActivity.class) //default: null (default error activity)
//                .eventListener(); //default: null
                .apply();


        PushManager.getInstance().initialize(this, null);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
        PushManager.getInstance().turnOnPush(this);
        //        PushManager.getInstance().registerPushIntentService(this, null);

    }

}