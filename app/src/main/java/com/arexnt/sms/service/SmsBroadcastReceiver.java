package com.arexnt.sms.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.arexnt.sms.common.Constant;
import com.arexnt.sms.common.SmsHelper;
import com.arexnt.sms.model.Message;
import com.arexnt.sms.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static android.R.attr.id;
import static com.arexnt.sms.common.SmsHelper.SMS_CONTENT_PROVIDER;


public class SmsBroadcastReceiver extends BroadcastReceiver{
    private final String TAG = "SmsBroadcastReceiver";

    private Context mContext;
    private SharedPreferences mPrefs;

    private String mAddress;
    private String mBody;
    private long mDate;

    private Uri mUri;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        abortBroadcast();

        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getExtras() != null) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            SmsMessage sms = messages[0];
            if (messages.length == 1 || sms.isReplace()) {
                mBody = sms.getDisplayMessageBody();
            } else {
                StringBuilder bodyText = new StringBuilder();
                for (SmsMessage message : messages) {
                    bodyText.append(message.getMessageBody());
                }
                mBody = bodyText.toString();
            }

            Log.d("msgFromReceiver", mBody);

            mAddress = sms.getDisplayOriginatingAddress();
            mDate = sms.getTimestampMillis();
            //检查验证码
            receiveCaptcha();
            //写入到数据库
            insertMessage();
        }

    }

    private void receiveCaptcha(){
        if (!StringUtils.isPersonalMoblieNO(mAddress)) {
            boolean isCpatchasMessage = false;
            if (!StringUtils.isContainsChinese(mBody)) {
                if (StringUtils.isCaptchasMessageEn(mBody) && !StringUtils.tryToGetCaptchasEn(mBody).equals("")) {
                    isCpatchasMessage = true;
                }
            } else if (StringUtils.isCaptchasMessage(mBody) && !StringUtils.tryToGetCaptchas(mBody).equals("")) {
                isCpatchasMessage = true;
            }
            if (isCpatchasMessage) {
                this.abortBroadcast();
                Message smsMessage = new Message();
                smsMessage.setContent(mBody);
                smsMessage.setSender(mAddress);
                Date date = new Date(mDate);
                smsMessage.setDate(date);
                String company = StringUtils.getContentInBracket(mBody, mAddress);
                if (company != null) {
                    smsMessage.setCompanyName(company);
                }
                smsMessage.setItemType(Constant.isMessage_in);
                //格式化短信日期提示
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm");
                //获得短信的各项内容
                String date_mms = dateFormat.format(date);
                smsMessage.setReceiveDate(date_mms);
                smsMessage.setReadStatus(0);
                smsMessage.setFromSmsDB(1);
                String captchas = StringUtils.tryToGetCaptchas(mBody);
                if (!captchas.equals("")) {
                    smsMessage.setCaptchas(captchas);
                }
                String resultContent = StringUtils.getResultText(smsMessage, false);
                if (resultContent != null) {
                    smsMessage.setResultContent(resultContent);
                }
                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                    smsMessage.save();
                }

                Intent intent = new Intent(mContext, ToastService.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("message", smsMessage);
                intent.putExtra("bundle", bundle);
                mContext.startService(intent);
            }
        }

    }

    private void insertMessage(){
        mUri = SmsHelper.addMessageToInbox(mContext, mAddress, mBody, mDate);
        long threadId = getThreadId(mUri);
        filterAddr(threadId);
    }

    private long getThreadId(Uri uri){
        Cursor getIdCursor = mContext.getContentResolver().query(uri, new String[]{SmsHelper.COLUMN_ID}, null, null, null);
        getIdCursor.moveToFirst();
        long id = getIdCursor.getLong(getIdCursor.getColumnIndexOrThrow(SmsHelper.COLUMN_ID));
        getIdCursor.close();
        Cursor getThreadIdCursor = null;
        long threadId = 0;
        try {
            getThreadIdCursor = mContext.getContentResolver().query(SMS_CONTENT_PROVIDER,
                    new String[]{SmsHelper.COLUMN_THREAD_ID}, "_id=" + id, null, null);
            getThreadIdCursor.moveToFirst();
            threadId = getThreadIdCursor.getLong(getThreadIdCursor.getColumnIndexOrThrow(SmsHelper.COLUMN_THREAD_ID));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            getThreadIdCursor.close();
        }
        Log.d(TAG,"thread id: " + threadId);
        return threadId;
    }

    private void filterAddr(long threadId){
        Set<String> mNotifList = mPrefs.getStringSet(Constant.NOTIF_SENDERS, new HashSet<String>());
        Set<String> mPersonalList = mPrefs.getStringSet(Constant.PERSONAL_SENDERS, new HashSet<String>());
        Set<String> newNotifList = new HashSet<>(mNotifList);
        Set<String> newPersonalList = new HashSet<>(mPersonalList);

        if (!mNotifList.contains(String.valueOf(threadId)) && !mPersonalList.contains(String.valueOf(threadId))){
            String pattern = "((^106)+?|(^10010)+?|(^10086)+?).*$";
            if (Pattern.matches(pattern, mAddress)){
                newNotifList.add(String.valueOf(id));
                mPrefs.edit().putStringSet(Constant.NOTIF_SENDERS, newNotifList).commit();
            }else {
                newPersonalList.add(String.valueOf(id));
                mPrefs.edit().putStringSet(Constant.PERSONAL_SENDERS, newPersonalList).commit();
            }
        }
    }
}
