package com.wzmt.ipaotui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zxf.libumshare.UMShareUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Activity mActivity;
    Bitmap bitmap;
    String TAG = "MainActivity";
    public static String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;


        PermissionsUtil.requestPermission(mActivity, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                init();
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                init();
            }
        }, needPermissions);


    }

    private void init() {
        TextView tv_wxlogin = findViewById(R.id.tv_wxlogin);
        TextView tv_qqlogin = findViewById(R.id.tv_qqlogin);
        TextView tv_share = findViewById(R.id.tv_share);
        TextView tv_share_single = findViewById(R.id.tv_share_single);
        TextView tv_share_xcx = findViewById(R.id.tv_share_xcx);

        tv_wxlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginByWX();
            }
        });
        tv_qqlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginByQQ();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.card_hongbao)).getBitmap();

                if (bitmap == null) {
                    Log.e(TAG, "bitmap为空");
                    return;
                }
                UMShareUtil.getInstance().shareImage(mActivity, bitmap);
            }
        });

        tv_share_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareUtil.getInstance().shareSinglePlatform(mActivity,
                        R.drawable.card_hongbao,
                        SHARE_MEDIA.WEIXIN,
                        "http://www.baidu.com",
                        "您好这是测试",
                        "这是内容");
            }
        });

        tv_share_xcx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String xcx_pages = "pages/share/share_receive?fid=106971";
                String  xcx_title = "新用户8元跑腿优惠券";
                bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.xcxshare)).getBitmap();
                UMShareUtil.getInstance().shareXCXPages(xcx_pages,xcx_title,bitmap);
            }
        });
    }

    private void loginByWX() {
        UMShareUtil.getInstance().WXQQlogin(mActivity, SHARE_MEDIA.WEIXIN, new UMShareUtil.GetWXQQUid() {
            @Override
            public void getUid(String uid) {
                Toast.makeText(mActivity, "wx--->uid=" + uid, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginByQQ() {
        UMShareUtil.getInstance().WXQQlogin(mActivity, SHARE_MEDIA.QQ, new UMShareUtil.GetWXQQUid() {
            @Override
            public void getUid(String uid) {
                Toast.makeText(mActivity, "qq--->uid=" + uid, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(mActivity).release();
    }
}
