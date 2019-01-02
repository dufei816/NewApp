package com.qimeng.huishou.newapp.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.qimeng.huishou.newapp.aidl.IMyConnect;

public class RemoteService extends Service {

    private static final String TAG = "";
    private MyBinder binder;
    private ServiceConnection conn;

    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    private void init() {
        if (conn == null) {
            conn = new MyConnection();
        }
        binder = new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "远程进程启动");
        Intent intents = new Intent();
        intents.setClass(this, LocalService.class);
        bindService(intents, conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    static class MyBinder extends IMyConnect.Stub {

        @Override
        public String getName() throws RemoteException {
            return "远程连接";
        }

    }

    class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "获取远程连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName nme) {
            Log.e(TAG, "本地连接被干掉了");
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);
        }
    }
}
