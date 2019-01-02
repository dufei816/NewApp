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

public class LocalService extends Service {

    private static final String TAG = "LocalService";
    private ServiceConnection conn;
    private MyService myService;

    @Override
    public IBinder onBind(Intent intent) {
        return myService;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    private void init() {
        if (conn == null) {
            conn = new MyServiceConnection();
        }
        myService = new MyService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "本地进程启动");
        Intent intents = new Intent();
        intents.setClass(this, RemoteService.class);
        bindService(intents, conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    class MyService extends IMyConnect.Stub {

        @Override
        public String getName() throws RemoteException {
            return null;
        }

    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "获取连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "远程连接被干掉了");
            LocalService.this.startService(new Intent(LocalService.this,RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this,RemoteService.class), conn, Context.BIND_IMPORTANT);
        }

    }

}
