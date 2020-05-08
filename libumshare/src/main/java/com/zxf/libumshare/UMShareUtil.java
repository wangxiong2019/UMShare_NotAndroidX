package com.zxf.libumshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * 创建 by hero
 * 时间 2020/3/30
 * 类名 友盟分享统计
 */
public class UMShareUtil {
    static String TAG = "UMShareUtil";

    public static UMShareUtil umShareUtil;

    public static UMShareUtil getInstance() {
        if (umShareUtil == null) {
            synchronized (UMShareUtil.class) {
                if (umShareUtil == null) {
                    umShareUtil = new UMShareUtil();
                }
            }
        }
        return umShareUtil;
    }

    Context mContext;


    public void init(Context mContext) {
        this.mContext = mContext;
    }

    public void initUM(String UMAppKey, String UMMessage_Secret, boolean isLog) {
        //友盟分享 第三方登录 推送 统计
        UMConfigure.setLogEnabled(isLog);
        UMConfigure.init(mContext, UMAppKey, "umeng",
                UMConfigure.DEVICE_TYPE_PHONE, UMMessage_Secret);

        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点

        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }


    static String WXAppId;      //微信 appId
    static String WXAppSecret;  //微信 appSecret
    static String QQAppId;      //QQ   appId
    static String QQAppSecret;  //QQ   appSecret

    public void initWXQQLogin(String WXAppId, String WXAppSecret, String QQAppId, String QQAppSecret) {
        //各个平台的配置，建议放在全局Application或者程序入口
        this.WXAppId = WXAppId;
        this.WXAppSecret = WXAppSecret;
        this.QQAppId = QQAppId;
        this.QQAppSecret = QQAppSecret;

        PlatformConfig.setWeixin(WXAppId, WXAppSecret);//第一个AppID  第二个AppSecret
        PlatformConfig.setQQZone(QQAppId, QQAppSecret);

    }

    private UMShareListener mShareListener;
    private ShareAction mShareAction;


    public void shareImage(final Activity mActivity, Bitmap bitmap) {
        if(bitmap==null){
            Log.e(TAG, "bitmap为空");
            return;
        }
        final UMImage image = new UMImage(mActivity, bitmap);//本地文件

        mShareListener = new CustomShareListener(mActivity);
        /*增加自定义按钮的分享面板*/
        mShareAction = new ShareAction(mActivity)
                .setDisplayList(
                        SHARE_MEDIA.WEIXIN,
                        SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QQ,
                        SHARE_MEDIA.QZONE
                )
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        new ShareAction(mActivity).withMedia(image)
                                .setPlatform(share_media)
                                .setCallback(mShareListener)
                                .share();

                    }
                });

        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
        mShareAction.open(config);
    }


    //分享单个平台
    public void shareSinglePlatform(final Activity mActivity, int logo,
                                    SHARE_MEDIA platform, String url, String title, String content) {

        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mActivity, "分享链接不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        mShareListener = new CustomShareListener(mActivity);

        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(content);
        web.setThumb(new UMImage(mActivity, logo));
        new ShareAction(mActivity)
                .withMedia(web)
                .setPlatform(platform)
                .setCallback(mShareListener)
                .share();


    }

    private class CustomShareListener implements UMShareListener {
        Activity mActivity;

        public CustomShareListener(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            Toast.makeText(mActivity, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mActivity, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.e("platform", platform + "throw:" + t.getMessage());
            }


        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            Toast.makeText(mActivity, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }


    //分享到小程序
    static String webpageUrl;//公司自己的官网
    static String Xcx_id;//小程序原始ID

    public void initXcx(String webpageUrl, String Xcx_id) {
        this.webpageUrl = webpageUrl;
        this.Xcx_id = Xcx_id;
    }


    public void shareXCXPages(String xcx_pages, String xcx_title, Bitmap xcx_bitmap) {
        if (TextUtils.isEmpty(Xcx_id)) {
            Toast.makeText(mContext, "小程序id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(xcx_pages)) {
            Toast.makeText(mContext, "小程序分享页面路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(xcx_title)) {
            Toast.makeText(mContext, "小程序分享标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (xcx_bitmap == null) {
            Toast.makeText(mContext, "小程序分享图片不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {


            Log.e("GoXCX", "xcx_pages=" + xcx_pages);

            WXMiniProgramObject miniProgram = new WXMiniProgramObject();
            miniProgram.webpageUrl = webpageUrl;//自定义
            miniProgram.userName = Xcx_id;
            miniProgram.path = xcx_pages;//页面路径以及参数

            WXMediaMessage mediaMessage = new WXMediaMessage(miniProgram);
            mediaMessage.title = xcx_title;//自定义
            mediaMessage.description = "自定义内容";//自定义

            Bitmap bitmap = zoomImage(xcx_bitmap, 500, 400);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] datas = baos.toByteArray();

            mediaMessage.thumbData = datas;

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "";
            req.scene = SendMessageToWX.Req.WXSceneSession;
            req.message = mediaMessage;

            IWXAPI wxApi = WXAPIFactory.createWXAPI(mContext, WXAppId);
            wxApi.sendReq(req);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "分享失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap zoomImage(Bitmap bgimage, double newWidth,
                             double newHeight) {
        Bitmap bitmap = null;
        try {
            // 获取这个图片的宽和高
            float width = bgimage.getWidth();
            float height = bgimage.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            if (width == 0) {
                width = 294;
                height = 294;
            }
            // 计算宽高缩放率
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                    (int) height, matrix, true);
            return bitmap;
        } catch (NullPointerException e) {

        }
        return bitmap;
    }


    /**
     * 微信  QQ登录
     */
    GetWXQQUid getWXQQUid;
    Activity mActivity;
    public void WXQQlogin(Activity mActivity,SHARE_MEDIA share_media,GetWXQQUid getWXQQUid){
        this.getWXQQUid=getWXQQUid;
        this.mActivity=mActivity;
        UMShareAPI.get(mActivity).getPlatformInfo(mActivity, share_media, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            Log.e("platform=", platform + "data=" + data.toString());
            String uid="";
            for (String key : data.keySet()) {
                Log.e("key", key + " : " + data.get(key));
                if (key.equals("uid")) {
                    uid = data.get(key);
                    Log.e("uid", uid);
                    getWXQQUid.getUid(uid);

                }
            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(mContext, "登录错误:" + t.getMessage(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

        }
    };

    public interface GetWXQQUid{
        void getUid(String uid);
    }
}
