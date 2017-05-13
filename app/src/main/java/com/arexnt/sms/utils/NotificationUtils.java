package com.arexnt.sms.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.arexnt.sms.R;
import com.arexnt.sms.common.StaticCaptchaCode;
import com.arexnt.sms.model.Message;



public class NotificationUtils implements StaticCaptchaCode {

    public static void showMessageInNotificationBar(Context context, Message message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_sms);
        builder.setAutoCancel(true);

        RemoteViews remoteViews;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view);

        remoteViews.setTextViewText(R.id.tv_title, message.getSender());

        if (message.getCaptchas() != null) {
            remoteViews.setTextViewText(
                    R.id.tv_content,
                    String.format(
                            context.getResources().getString(R.string.notify_msg),
                            message.getCaptchas()
                    )
            );
        } else {
            remoteViews.setTextViewText(R.id.tv_content, message.getContent());
        }
        builder.setContent(remoteViews);
        Notification notification = builder.build();
        //设定Notification出现时的声音
        notification.defaults |= Notification.DEFAULT_SOUND;
        //设定如何振动
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        Intent notificationIntent = new Intent(ACTION_CLICK);
        notificationIntent.putExtra("captchas", message.getCaptchas());
        PendingIntent broadcast = PendingIntent.getBroadcast(
                context, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );
        notification.contentView.setOnClickPendingIntent(R.id.tv_content, broadcast);
        int mNotificationId = 2013055371;
        notificationManager.notify(mNotificationId, notification);
    }
}
