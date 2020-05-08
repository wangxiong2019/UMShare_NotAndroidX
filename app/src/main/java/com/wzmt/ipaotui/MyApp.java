package com.wzmt.ipaotui;

import android.app.Application;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.zxf.libumshare.UMShareUtil;
import com.wzmt.ipaotui.push.UmengNotificationService;

/**
 * 创建 by hero
 * 时间 2020/3/30
 * 类名
 */
public class MyApp extends Application  {
    String UM_Push_AppKey = "5e1c48ff0cafb234960000d9";
    String UM_Message_Secret = "c79a2e4c07cf4a5e76be5878870f686e";

    String WX_AppId="wxca6c41985ef564a2";
    String WX_AppSecret="f9fd9a3b89c214e5f44c541bb6da7c43";
    String QQ_AppId="1103679224";
    String QQ_AppSecret="aqME7310PNxgI3Qt";

    public static String Xcx_AppId="gh_f8741c74cfd6";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyApp", "onCreate-------->  " );
        initUm();
    }

    private void initUm() {

        UMShareUtil.getInstance().init(getApplicationContext());
        UMShareUtil.getInstance().initUM(UM_Push_AppKey, UM_Message_Secret, true);
        UMShareUtil.getInstance().initWXQQLogin(WX_AppId, WX_AppSecret, QQ_AppId, QQ_AppSecret);
        UMShareUtil.getInstance().initXcx("http://www.ipaotui.com",Xcx_AppId);
        initPush();
        initAgent();
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

    //页面统计
    private void initAgent(){
        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点

        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}
