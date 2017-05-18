package com.arexnt.sms.common;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import static com.arexnt.sms.common.Constant.RECEIVED_MESSAGE_CONTENT_PROVIDER;

/**
 * Created by arexnt on 2017/5/18.
 */

public class SmsHelper {


    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_THREAD_ID = "thread_id";

    public static final Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/");

    /**
     * Add incoming SMS to inbox
     *
     * @param context
     * @param address Address of sender
     * @param body    Body of incoming SMS message
     * @param time    Time that incoming SMS message was sent at
     */
    public static Uri addMessageToInbox(Context context, String address, String body, long time) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put("address", address);
        cv.put("body", body);
        cv.put("date_sent", time);

        return contentResolver.insert(RECEIVED_MESSAGE_CONTENT_PROVIDER, cv);
    }
}
