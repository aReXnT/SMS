package com.arexnt.sms.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.arexnt.sms.utils.DateFormatter;
import com.arexnt.sms.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arexnt.sms.ui.messagelist.MessageListActivity.MMS_SMS_CONTENT_PROVIDER;
import static com.arexnt.sms.ui.messagelist.MessageListActivity.PROJECTION;

public class DataMessageHelper {
    public String id;
    public Context mContext;
    public DataMessageHelper(Context context){
        this.mContext = context;
    }

    public HashMap<String,String> getDataMessageFromId(String id){
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor mAddrCursor = contentResolver.query(Uri.withAppendedPath(MMS_SMS_CONTENT_PROVIDER,
                String.valueOf(id)),
                PROJECTION, null, null, "date DESC");
        HashMap<String,String> dataMessage = new HashMap<>();
        if(mAddrCursor.moveToFirst()){
            do{
                String msgContent = "";
                try {
                    msgContent = new String(mAddrCursor.getString(mAddrCursor.getColumnIndexOrThrow("body")).getBytes(),"UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringTokenizer stringTokenizer = new StringTokenizer(msgContent,"\r");
                while (stringTokenizer.hasMoreTokens()){
                    String s = stringTokenizer.nextToken();
//                        Log.d("originalToken",s);
                    if (StringUtils.isDataMessage(s)){
//                        Log.d("isDataTokenizer",s);
                        Matcher m = Pattern.compile("(\\w+\\.\\w+)((MB)?(KB)?(GB)?)").matcher(s);
                        Matcher keyword = Pattern.compile(Constant.RE_DATA_KEYWORD).matcher(s);
                        String dataNum = "";
                        while (m.find()){
                            dataNum = m.group();
                        }
                        String kwStr = "";
                        while (keyword.find()){
                            kwStr = keyword.group();
                        }
                        dataMessage.put(kwStr, dataNum);
//                        Log.d("isDataTokenizer",s + "\n keyword:"+ kwStr + " ,Data is: " + dataNum);
                    }
                }
                if (!dataMessage.isEmpty()){
                    long date = mAddrCursor.getLong(mAddrCursor.getColumnIndex("date"));
                    dataMessage.put("date", DateFormatter.getMessageTimestamp(mContext, date));
                    break;
                }
            }while (mAddrCursor.moveToNext());
        }

            return dataMessage;
    }
}
