package com.arexnt.sms.utils;

import android.widget.Toast;

import com.arexnt.sms.SMSApp;

/**
 * Created by arexnt on 2017/5/2.
 */

public class ToastUtils {
    private ToastUtils() {
    }

    public static void showShort(int resId) {
        Toast.makeText(SMSApp.getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(String message) {
        Toast.makeText(SMSApp.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int resId) {
        Toast.makeText(SMSApp.getContext(), resId, Toast.LENGTH_LONG).show();
    }

    public static void showLong(String message) {
        Toast.makeText(SMSApp.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
