package com.qimeng.huishou.newapp;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.qimeng.huishou.newapp.net.HttpUtil;
import com.qimeng.huishou.newapp.util.MySharedPreferences;

import io.reactivex.schedulers.Schedulers;

public class DemoIntentService extends GTIntentService {

    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.e(TAG, msg.toString());
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e(TAG, cmdMessage.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        String content = msg.getContent();
        String msgid = msg.getMessageId();
        String taskId = msg.getTaskId();
        String title = msg.getTitle();

        HttpUtil.getInstance().getApi().pushxy(MySharedPreferences.getInstance().getCode(), title, content, true)
                .subscribeOn(Schedulers.io())
                .subscribe(data -> {

                }, error -> {

                });
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        Log.e(TAG, msg.toString());
    }
}