package com.zxf.umshare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zxf.libumshare.UMShareUtil;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Activity mActivity;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, null);


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
                UMShareUtil.getInstance().shareImage(mActivity, bitmap);
            }
        });

        tv_share_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareUtil.getInstance().shareSinglePlatform(mActivity, R.mipmap.ic_launcher,
                        SHARE_MEDIA.WEIXIN, "www.baidu.com", "您好这是测试", "这是内容");
            }
        });
    }

    private void loginByWX(){

    }

    private void loginByQQ(){

    }
}
