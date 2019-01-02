package com.qimeng.huishou.newapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.qimeng.huishou.newapp.net.HttpUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MySharedPreferences {

    private static SharedPreferences preferences;
    private static MySharedPreferences mySharedPreferences;
    private static Gson gson;

    private MySharedPreferences() {
        preferences = App.context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance() {
        synchronized (MySharedPreferences.class) {
            if (preferences == null) {
                mySharedPreferences = new MySharedPreferences();
            }
            return mySharedPreferences;
        }
    }

    public void putCode(String code) {
        HttpUtil.getInstance().getApi().updateJqm(code, PushManager.getInstance().getClientid(App.context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("JQCode", code);
                    editor.commit();
                    Toast.makeText(App.context, "设置成功", Toast.LENGTH_SHORT).show();
                },error->{

                });
    }

    public String getCode() {
        return preferences.getString("JQCode", "");
    }

}
