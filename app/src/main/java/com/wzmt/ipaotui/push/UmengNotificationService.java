package com.wzmt.ipaotui.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.umeng.message.UmengMessageService;
import com.wzmt.ipaotui.MainActivity;
import com.wzmt.ipaotui.R;

import org.android.agoo.common.AgooConstants;

import androidx.core.app.NotificationCompat;


public class UmengNotificationService extends UmengMessageService {
    String TAG = "UmengNotificationService";
    private String order_id = "", type = "";

    Intent myintent;
    Context mContext;

    String message;
    String TITLE = "";

    @Override
    public void onMessage(Context context, Intent intent) {
        mContext = context;
        String MESSAGE_BODY = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        //处理消息内容
        Log.e(TAG, "=" + MESSAGE_BODY);

        PushManager("收到友盟推送:" + MESSAGE_BODY);


    }


    private void PushManager(String title) {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        String mChannelId = "UMPush_ID";
        //TODO 重点1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getResources().getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(mChannelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        Intent bIntent = new Intent(mContext, MainActivity.class);

        PendingIntent pbIntent = PendingIntent.getActivity(mContext, 1, bIntent, PendingIntent.FLAG_ONE_SHOT);
        //TODO 重点2:此处需要保证使用最新的版本，
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(mContext, mChannelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(mContext.getResources().getString(R.string.app_name))
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setContentIntent(pbIntent);
        Notification notification = bBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(1, notification);
    }

}
