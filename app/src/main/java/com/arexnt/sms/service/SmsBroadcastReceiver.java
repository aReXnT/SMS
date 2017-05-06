package com.arexnt.sms.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.arexnt.sms.common.Constant;
import com.arexnt.sms.model.Message;
import com.arexnt.sms.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SmsBroadcastReceiver extends BroadcastReceiver{

    private Intent mIntent;
    private String content;

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object p : pdus) {
            byte[] sms = (byte[]) p;
            SmsMessage message = SmsMessage.createFromPdu(sms);
            //获取短信内容
             content = message.getMessageBody();
            Log.d("msgFromReceiver", content);
            //获取发送时间
            final Date date = new Date(message.getTimestampMillis());
            final String sender = message.getOriginatingAddress();

            if (!StringUtils.isPersonalMoblieNO(sender)) {
                boolean isCpatchasMessage = false;
                if (!StringUtils.isContainsChinese(content)) {
                    if (StringUtils.isCaptchasMessageEn(content) && !StringUtils.tryToGetCaptchasEn(content).equals("")) {
                        isCpatchasMessage = true;
                    }
                } else if (StringUtils.isCaptchasMessage(content) && !StringUtils.tryToGetCaptchas(content).equals("")) {
                    isCpatchasMessage = true;
                }
                if (isCpatchasMessage) {
                    this.abortBroadcast();
                    Message smsMessage = new Message();
                    smsMessage.setContent(content);
                    smsMessage.setSender(sender);
                    smsMessage.setDate(date);
                    String company = StringUtils.getContentInBracket(content, sender);
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
                    String captchas = StringUtils.tryToGetCaptchas(content);
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
                    mIntent = new Intent(context, ToastService.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("message", smsMessage);
                    mIntent.putExtra("bundle", bundle);
                    context.startService(mIntent);
                }
            }

        }
    }
}
