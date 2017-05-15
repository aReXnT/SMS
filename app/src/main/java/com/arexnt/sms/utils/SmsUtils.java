package com.arexnt.sms.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.arexnt.sms.common.Constant;
import com.arexnt.sms.model.Message;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SmsUtils {
    Context mContext;

    public SmsUtils(final Context context){
        mContext = context;
    }

    //只检查收件箱的验证码信息
    public static final Uri MMSSMS_ALL_MESSAGE_URI = Uri.parse("content://sms/inbox");
    public static final Uri ALL_MESSAGE_URI = MMSSMS_ALL_MESSAGE_URI.buildUpon().
            appendQueryParameter("simple", "true").build();

    public static final String[] SMS_THREADS_PROJECTION = {
            "_id", "address", "person", "body",
            "date", "type", "thread_id"};

    public List<Message> getAllCaptchMessages() {
        List<String> dateGroups = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
//        BlockedConversationHelper helper = new BlockedConversationHelper(null);
        Cursor cursor = contentResolver.query(ALL_MESSAGE_URI, SMS_THREADS_PROJECTION,
                null, null, "date desc");
        List<Message> smsMessages = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                int indexBody = cursor.getColumnIndex("body");
                int indexAddress = cursor.getColumnIndex("address");
                int indexThreadId = cursor.getColumnIndex("thread_id");
                String strbody = cursor.getString(indexBody);
                String strAddress = cursor.getString(indexAddress);
                if (!StringUtils.isPersonalMoblieNO(strAddress)) {//个人号码的短信包含验证码
                    boolean isCpatchasMessage = false;
                    //判断是否包含验证码
                    if (!StringUtils.isContainsChinese(strbody)) {//是否包含中文
                        //不包含中文就是英文的方式解析
                        if (StringUtils.isCaptchasMessageEn(strbody) && !StringUtils.tryToGetCaptchasEn(strbody).equals("")) {
                            //判断是不是一条英文的验证码短信且验证码内容不为空。
                            isCpatchasMessage = true;//满足则标记为验证码短信
                        }
                        //判断中文短信同理
                    } else if (StringUtils.isCaptchasMessage(strbody) && !StringUtils.tryToGetCaptchas(strbody).equals("")) {
                        isCpatchasMessage = true;
                    }
                    if (isCpatchasMessage) {
                        int date = cursor.getColumnIndex("date");
                        //格式化短信日期提示
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm");
                        Date formatDate = new Date(Long.parseLong(cursor.getString(date)));
                        long threadId = cursor.getLong(indexThreadId);

                        //获得短信的各项内容
                        String dateMms = dateFormat.format(formatDate);
                        Message message = new Message();
                        String company = StringUtils.getContentInBracket(strbody, strAddress);
                        if (company != null) {
                            message.setCompanyName(company);
                        }
                        String captchas = StringUtils.tryToGetCaptchas(strbody);
                        if (!captchas.equals("")) {
                            message.setCaptchas(captchas);
                        }
                        int columnIndex = cursor.getColumnIndex("_id");
                        String smsId = cursor.getString(columnIndex);
//                    message.setIsMessage(true);
                        message.setItemType(Constant.isCaptcha);
                        message.setDate(formatDate);
                        message.setSender(strAddress);
                        message.setThreadId(threadId);
                        message.setContent(strbody);
                        message.setSmsId(smsId);
                        message.setReceiveDate(dateMms);
                        String resultContent = StringUtils.getResultText(message, false);
                        if (resultContent != null) {
                            message.setResultContent(resultContent);
                        }
                        //检查收件箱地址把所有的验证码短信放到smsMessages
                        smsMessages.add(message);
                    }
                }
            }while (cursor.moveToNext());
        }



        List<Message> localMessages = DataSupport.where("readStatus = ?", "0").order("date asc").find(Message.class);
        for (Message message : localMessages) {
            if (message.getDate() != null) {
//                message.setIsMessage(true);
                message.setItemType(Constant.isCaptcha);
                boolean find = false;
                for (int u = 0; u < smsMessages.size(); u++) {
                    if (message.getDate().getTime() > smsMessages.get(u).getDate().getTime()) {
                        smsMessages.add(u, message);
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    smsMessages.add(message);
                }

            }
        }


        List<Message> unionMessages = new ArrayList<>();

        for (Message message : smsMessages) { //意思是 遍历smsMessage List里面的每一个message元素
            //这里按日期设置分组
            String group = TimeUtils.getInstance().getDateGroup(message.getDate());//按每一条短信的日期定义不同分组
            if (dateGroups.size() == 0) {//第一次跑的时候 dateGroup列表是空的
                dateGroups.add(group);// 为空的列表添加分组
                Message dateMessage = new Message();
                dateMessage.setReceiveDate(group);//每天短信的结构体中都包含一个设定不同分组的字符串
//                dateMessage.setIsMessage(false);//标记为“不是短信”
                dateMessage.setItemType(Constant.isCaptcha_Sep);
                unionMessages.add(dateMessage);//插入到短信列表中，这是一个标签时间的元素，不是短信。
            } else {
                if (!group.equals(dateGroups.get(dateGroups.size() - 1))) {
                    //在第二次运行的时候，要和第一次运行的进行对比，list.get(0)获取的是第一个元素，
                    //获取短信时用到是DESC倒序排序，则最后收到的排最前
                    //当get到相同的dataGroup，就直接把message加入到列表中
                    //当dateGroup不同的时候，就添加一条不是短信的标签到列表中
                    dateGroups.add(group);
                    Message dateMessage = new Message();
                    dateMessage.setReceiveDate(group);
//                    dateMessage.setIsMessage(false);
                    dateMessage.setItemType(Constant.isCaptcha_Sep);
                    unionMessages.add(dateMessage);
                }
            }
            unionMessages.add(message);
        }

        cursor.close();
        return unionMessages;
    }
}
