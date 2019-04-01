package com.qimeng.huishou.newapp;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwit.HwitManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qimeng.huishou.newapp.aidl.IMyConnect;
import com.qimeng.huishou.newapp.entity.User;
import com.qimeng.huishou.newapp.net.HttpUtil;
import com.qimeng.huishou.newapp.service.OnePixelReceiver;
import com.qimeng.huishou.newapp.util.Code;
import com.qimeng.huishou.newapp.util.Config;
import com.qimeng.huishou.newapp.util.EncryptUtil;
import com.qimeng.huishou.newapp.util.ModeUtil;
import com.qimeng.huishou.newapp.util.MySharedPreferences;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

@SuppressLint("CheckResult")
@RequiresApi(api = Build.VERSION_CODES.FROYO)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_Msg";

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_ping_count)
    TextView tvPingCount;
    @BindView(R.id.ll_double_ping)
    LinearLayout llDoublePing;
    @BindView(R.id.tv_name2)
    TextView tvName2;
    @BindView(R.id.tv_zhi)
    TextView tvZhi;
    @BindView(R.id.ll_double_zhi)
    LinearLayout llDoubleZhi;
    @BindView(R.id.ll_double)
    LinearLayout llDouble;
    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_type_name)
    TextView tvTypeName;
    @BindView(R.id.tv_tou_data)
    TextView tvTouData;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.iv_images)
    ImageView ivImages;
    @BindView(R.id.iv_image1)
    ImageView ivImage1;
    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.tv_debug)
    TextView tvDebug;
    @BindView(R.id.tv_showMsg)
    TextView tvShowMsg;
    @BindView(R.id.sfview)
    SurfaceView sfview;

    private static final String key = "9ba45bfd500642328ec03ad8ef1b6e751234567890qwertaaaaaa";

    private static final String HUAN_BAO = "/mnt/sdcard/huanbao.mp4";
    //    private static final String HUI_SHOU = "/mnt/sdcard/huishou.mp4";
    private static final String HUI_SHOU = "/mnt/sdcard/chanpin.mp4";

    private boolean isImage = false;

    private boolean conn = false;
    private int video = 0;
    private boolean videoPlay = false;

    private User[] users = new User[2];

    private Integer[] imageRes = {
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
            R.drawable.image0};

    private ImageLoader loader;

    private Disposable modSub; // 显示
    private Disposable modConn; // 连接

    private MediaPlayer player;

    private MyReceiver myReceiver;
    private ComponentName adminReceiver;
    private PowerManager mPowerManager;
    private DevicePolicyManager policyManager;
    private PowerManager.WakeLock mWakeLock;
    private boolean lock = false;
    private EncryptUtil baseUtil;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        modConn.dispose();
        modSub.dispose();
        unregisterReceiver(myReceiver);
        Log.e(TAG, "onDestroy()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            baseUtil = new EncryptUtil(key, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
        startMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()");
    }


    private void startMode() {
        modSub = Observable.interval(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (lock) return;
                    if (checkUser()) return;
                    setDefaultMain(1);
                    if (users[0] != null) {
                        if (users[1] != null) {
                            setDouble();
                        } else {
                            setOne(users[0], Config.OPEN_PING);
                        }
                        return;
                    }
                    if (users[1] != null) {
                        if (users[0] != null) {
                            setDouble();
                        } else {
                            setOne(users[1], Config.OPEN_ZHI);
                        }
                    }
                }, error -> {
                    Log.e(TAG, error.getMessage());
                });
    }

    // SetBHxxxxxxxxx
    private void startCode(String code, int type) {
        if (lock) {
            checkScreenOn();
        }
        if (code.indexOf("SetBH") != -1) {
            code = code.substring(5);
            MySharedPreferences.getInstance().putCode(code);
            return;
        }
        int i = 0;
        i = (type == Config.OPEN_PING) ? 1 : 2;
        if (code.equals("999")) {
            ModeUtil.getInstance().openMen();
            return;
        }
        //先进行单机测试
        if (conn) {
            if (code.indexOf("code=") == -1) return;
            String finalCode = code.substring(code.indexOf("code="), code.indexOf("&card")).split("=")[1];
            HttpUtil.getInstance().getApi().getUser(finalCode, i + "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(user -> {
                        if (TextUtils.isEmpty(user.getXm())) {
                            Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(user -> {
                        user.setCode(finalCode);
                        runing(type, user);
                    }, error -> {
                        Log.e(TAG, "method=getUser-->" + error.getMessage());
                    });
        } else {
            Toast.makeText(this, "网络不通畅", Toast.LENGTH_SHORT).show();
        }
    }

    private synchronized void runing(int type, User user) {
        if (user != null && !TextUtils.isEmpty(user.getXm())) {
            stopVideo();
            switch (type) {
                case Config.OPEN_PING:
                    if (users[0] != null) {
                        Toast.makeText(this, "瓶口正在使用请稍后", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.setCount(-1);
                    users[0] = user;
                    ModeUtil.getInstance().openPingMode(new ModeUtil.Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            upDataPing(users[0], data);
                        }

                        @Override
                        public void onError(String msg) {
                            users[0] = null;
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show());
                        }
                    });
                    break;
                case Config.OPEN_ZHI:
                    if (users[1] != null) {
                        Toast.makeText(this, "纸口正在使用请稍后", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.setWeight(-1);
                    users[1] = user;
                    ModeUtil.getInstance().openZhiMode(new ModeUtil.Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            upDataZhi(users[1], data < 0 ? 0 : data);
                        }

                        @Override
                        public void onError(String msg) {
                            users[1] = null;
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show());
                        }
                    });
                    break;
            }
        }
    }

    private void upDataZhi(User user, int zhi) {
        if (user != null) {
            user.setWeight(zhi);
        }
        if (conn) {
            uploadIntegral(user, "2", zhi + "");
        }
    }

    private void uploadIntegral(User user, String clas, String num) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(user.getCode() + ":");
        buffer.append(clas + ":");
        buffer.append(num + ":");
        buffer.append(MySharedPreferences.getInstance().getCode());

        String code = baseUtil.encode(buffer.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), code);

        HttpUtil.getInstance().getApi().uploadIntegral(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Observable.timer(3, TimeUnit.SECONDS)
                            .observeOn(Schedulers.newThread())
                            .subscribe(l -> {
                                if (clas.equals("2")) {
                                    users[1] = null;
                                } else {
                                    users[0] = null;
                                }
                            });
                    Log.e("MSG", data.getMsg() + "");
                }, error -> {
                    Observable.timer(3, TimeUnit.SECONDS)
                            .observeOn(Schedulers.newThread())
                            .subscribe(l -> {
                                if (clas.equals("2")) {
                                    users[1] = null;
                                } else {
                                    users[0] = null;
                                }
                            });
                    Log.e(TAG, "上传失败");
                });
    }

    private synchronized void upDataPing(User user, int ping) {
        if (user != null) {
            user.setCount(ping);
        }
        if (conn) {
            uploadIntegral(user, "1", ping + "");
        } else {
            Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
        }
    }


    private void stopVideo() {
        if (videoPlay && player != null) {
            player.stop();
            player.release();
            player = null;
            video = 0;
            videoPlay = false;
        }
        sfview.setVisibility(View.GONE);
        ivImages.setVisibility(View.GONE);
    }

    private void setDefaultMain(int type) {
        if (type == 1) {
            llBg.setBackgroundResource(R.drawable.bg2);
        } else {
            llBg.setBackgroundResource(R.drawable.bg);
        }
    }

    private void setDouble() {
        llDouble.setVisibility(View.VISIBLE);
        llData.setVisibility(View.GONE);
        tvName.setText(users[0].getXm());
        tvName2.setText(users[1].getXm());
        if (users[0].getCount() == -1) {
            tvPingCount.setText("投瓶数量：请稍后···");
        } else {
            tvPingCount.setText("投瓶数量：" + users[0].getCount() + "瓶");
        }
        if (users[1].getWeight() == -1) {
            tvZhi.setText("投纸重量：请稍后···");
        } else {
            tvZhi.setText("投纸重量：" + users[1].getWeight() + "克");
        }
    }

    private void setOne(User user, int type) {
        llData.setVisibility(View.VISIBLE);
        llDouble.setVisibility(View.GONE);
        if (type == Config.OPEN_PING) {
            ivType.setImageResource(R.mipmap.touping);
            if (user.getCount() == -1) {
                tvTouData.setText("投瓶数量：请稍后···");
            } else {
                tvTouData.setText("投瓶数量：" + user.getCount() + "瓶");
            }
        } else {
            ivType.setImageResource(R.mipmap.touzhi);
            if (user.getWeight() == -1) {
                tvTouData.setText("投纸重量：请稍后···");
            } else {
                tvTouData.setText("投纸重量：" + user.getWeight() + "克");
            }
        }
        tvTypeName.setText(user.getXm());
    }


    private void startImage() {
        Observable.fromArray(imageRes)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(__ -> {
                    isImage = true;
                    ivImages.setVisibility(View.VISIBLE);
                    ivImages.setImageResource(R.drawable.image1);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Thread.sleep(4000);
                    runOnUiThread(() -> loader.displayImage("drawable://" + res, ivImages));
                }, error -> {
                    Thread.sleep(4000);
                    video = 0;
                    videoPlay = false;
                    isImage = false;
                    runOnUiThread(() -> ivImages.setVisibility(View.GONE));
                }, () -> {
                    Thread.sleep(4000);
                    video = 0;
                    videoPlay = false;
                    isImage = false;
                    runOnUiThread(() -> ivImages.setVisibility(View.GONE));
                });
    }


    private void init() {
        tvDebug.setVisibility(View.GONE);
        tvShowMsg.setVisibility(View.GONE);
//        LogUtil.initView(tvDebug, tvShowMsg);
        modConn = Observable.interval(20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(__ -> {
                    conn = HttpUtil.getInstance().isConnetction();
                })
                .subscribe(l -> conn = HttpUtil.getInstance().isConnetction(), error -> Log.e(TAG, error.getMessage()));
        loader = ImageLoader.getInstance();
        ImageLoaderConfiguration configuration_0 = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration_0);
        HwitManager.HwitSetHideSystemBar(this);//以藏导航
        HwitManager.HwitSetSlientInstallApp(1);//静默安装
//        boolean data = HwitManager.HwitSetBootupAppNameWhenPoweron(null, null);

        new Code("/dev/ttyS1", string -> {
            Observable.just(string)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(str -> startCode(str, Config.OPEN_ZHI));
        });

        new Code("/dev/ttyS4", string -> {
            Observable.just(string)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(str -> startCode(str, Config.OPEN_PING));
        });

        adminReceiver = new ComponentName(this, ScreenOffAdminReceiver.class);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(myReceiver, intentFilter);

        checkAndTurnOnDeviceManager();
        checkScreenOn();
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {//息屏
                lock = true;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {//亮屏
                lock = false;
            }
        }
    }

    public void checkAndTurnOnDeviceManager() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "锁屏所需权限");
        startActivityForResult(intent, 0);
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("InvalidWakeLockTag")
    public void checkScreenOn() {
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
        mWakeLock.release();
    }

    public void checkScreenOff() {
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            showToast("没有设备管理权限");
        }
    }


    private void initPlay(String path) {
        player = new MediaPlayer();
        sfview.setVisibility(View.VISIBLE);
        try {
            player.setDataSource(path);
            sfview.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    player.setDisplay(surfaceHolder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });
            player.setOnPreparedListener(mediaPlayer -> {
                player.start();
            });
            player.setOnCompletionListener(mediaPlayer -> {
                sfview.setVisibility(View.GONE);
                player.stop();
                player.release();
                player = null;
                startImage();
            });
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean checkUser() {
        if (users[0] == null && users[1] == null) {
            setDefaultMain(0);
            llDouble.setVisibility(View.GONE);
            llData.setVisibility(View.GONE);
            video++;
            if (video > 75 && !isImage && !videoPlay) {
                video = 0;
                videoPlay = true;
                initPlay(HUAN_BAO);
            }
            return true;
        } else {
            video = 0;
            return false;
        }
    }


}


