package com.zxf.umshare;

import android.app.Application;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.zxf.libumshare.UMShareUtil;
import com.zxf.libumshare.UMTokenBack;
import com.zxf.umshare.push.UmengNotificationService;

/**
 * 创建 by hero
 * 时间 2020/3/30
 * 类名
 */
public class MyApp extends Application implements UMTokenBack {
    String UMPush_AppKey = "5e81bced978eea06fd7fc4a6";
    String Message_Secret = "04888162fe3a4431a542a33f8c892614";
    public static String UMToekn = "";

    @Override
    public void onCreate() {
        super.onCreate();

        initUm();
    }

    private void initUm() {
        UMShareUtil.getInstance().init(getApplicationContext());
        UMShareUtil.getInstance().initUM(UMPush_AppKey, Message_Secret, true);

        UMShareUtil.getInstance().initWXQQLogin(UMPush_AppKey, Message_Secret, "", "");


        initPush();
    }
    private void initPush(){
        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.e("initPush", "注册成功3：deviceToken：-------->  " + deviceToken);

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("initPush", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
        mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);
    }
    @Override
    public void getUMToken(String token) {
        UMToekn = token;
    }
}
