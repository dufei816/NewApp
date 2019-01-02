package com.qimeng.huishou.newapp.net;

import android.util.Log;

import com.qimeng.huishou.newapp.util.Config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtil {

    private static HttpUtil myHttpUtil;
    private static final String TAG = "HttpUtil";
    private Http api;

    public Http getApi() {
        return api;
    }

    private HttpUtil() {
        init();
    }

    public boolean isConnetction() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec("ping -c 3 www.baidu.com");
        int ret = p.waitFor();
        if (ret == 0) {
            return true;
        }
        return false;
    }

    private void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Config.OUT_TIME, TimeUnit.SECONDS);//连接 超时时间
        builder.writeTimeout(Config.OUT_TIME, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(Config.OUT_TIME, TimeUnit.SECONDS);//读操作 超时时间
        builder.retryOnConnectionFailure(true);//错误重连

        Interceptor tor = chain -> {
            Request request = chain.request();
            Log.e(TAG, request.url().toString());
            return chain.proceed(request);
        };

        builder.addInterceptor(tor);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Config.BASE_URL)
                .build();
        api = retrofit.create(Http.class);
    }

    public static HttpUtil getInstance() {
        synchronized (HttpUtil.class) {
            if (myHttpUtil == null) {
                myHttpUtil = new HttpUtil();
            }
            return myHttpUtil;
        }
    }

}
