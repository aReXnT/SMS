package com.arexnt.sms.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by arexnt on 2017/5/2.
 */

public class ClipboardUtils {

    public static void putTextIntoClipboard(Context context, String text){
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("复制", text);
        clipboardManager.setPrimaryClip(clipData);
    }
}
