package com.arexnt.sms.common;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.arexnt.sms.utils.DateFormatter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arexnt.sms.utils.SmsUtils.ALL_MESSAGE_URI;
import static com.arexnt.sms.utils.SmsUtils.SMS_THREADS_PROJECTION;


public class ExpressMessageHelper {
    private String id;
    private Context mContext;
    private SharedPreferences mPreferences;
    public ExpressMessageHelper(Context context, SharedPreferences preferences){
        this.mContext = context;
        this.mPreferences = preferences;
    }

    public HashMap<String, String> getExpressMessage(){
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ALL_MESSAGE_URI,
                SMS_THREADS_PROJECTION,
                null, null, "date desc");
        HashMap expressMessage = new HashMap();
        if (cursor.moveToFirst()){
            do {
                String content = cursor.getString(cursor.getColumnIndex("body"));
                String expressCompany = mPreferences.getString(Constant.EXPRESS_COMPANY, Constant.RE_EXPRESS_COMPANY_KEYWORD);

                if (content.contains("菜鸟驿站")){
                    Matcher companyRe = Pattern.compile(expressCompany).matcher(content);
                    if (companyRe.find()){
                        expressMessage.put("company", companyRe.group());
                    }
                    Matcher code = Pattern.compile("[0-9\\.]+").matcher(content);
                    while (code.find()){
                        String strCode = code.group();
                        if (code.group().length() > 4 && code.group().length() < 8 && !code.group().contains(".")){
                            expressMessage.put("code",code.group());
                        }
                    }
                    String disamguation = mPreferences.getString(Constant.EXPRESS_DISAMBIGUATION, Constant.RE_EXPRESS_DISAMBIGUATION);
                    Matcher isInLocker = Pattern.compile(disamguation).matcher(content);
                    if (isInLocker.find()){
                        expressMessage.put("isInLocker", true);
                    }else {
                        expressMessage.put("isInLocker", false);
                    }
                }

                if (!expressMessage.isEmpty()){
                    long date = cursor.getLong(cursor.getColumnIndex("date"));
                    expressMessage.put("date",DateFormatter.getMessageTimestamp(mContext, date));
                    break;
                }

            }while (cursor.moveToNext());
        }

        return expressMessage;
    }


    private String isNearToCode(String content){
        Matcher code = Pattern.compile("[0-9\\.]+").matcher(content);
        while (code.find()){
            if (code.group().length() > 4 && code.group().length() < 8 && !code.group().contains(".")){

            }
        }
        return null;
    }

    private String[] getKeyword(){

        return null;
    }
}
