package com.arexnt.sms.common;

import android.net.Uri;

/**
 * Created by arexnt on 2017/4/18.
 */

public class Constant {
    //SharePreference
    public static final String PERSONAL_SENDERS = "pref_key_personal_senders";
    public static final String BLOCKED_SENDERS = "pref_key_blocked_senders";
    public static final String NOTIF_SENDERS = "pref_key_notif_senders";
    public static final String KEY_ENABLE_AUTO_COPY = "auto_copy_captcha";
    public static final String KEY_ENABLE_CONFIRM_DEL="pref_key_confirm_del";

    //MMS-SMS URI
    public static final Uri ALL_CONVERSATION = Uri.parse("content://mms-sms/conversations");
    public static final Uri CONVERSATIONS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations?simple=true");
    public static final Uri ADDRESSES_CONTENT_PROVIDER = Uri.parse("content://mms-sms/canonical-addresses");
    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");
    public static final String SMS_URI = "content://sms/";
    public static final String SMS_SENT_URI = "content://sms/sent";
    //DATABASE
    public static final String PDUS = "pdus";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_BODY = "body";
    public static final String DEFAULT_SORT_ORDER = "date DESC";
    public static final int COLUMN_ADDRESSES_ADDRESS = 1;
    public static final int LOADER_ID = 1;

    public static final String PERSONAL_LIST = "personal";
    public static final String NOTIF_LIST = "notif";
    public static final String BLOCKED_LIST = "block";

    public static final int isCaptcha_Sep = 0;
    public static final int isCaptcha = 1;
    public static final int isMessage_in = 2;
    public static final int isMessage_out = 3;

    public static final int LOADER_CONVERSATIONS = 0;
    public static final int LOADER_MESSAGES = 1;
    public static final int LOADER_CAPTCHA = 2;

    //Permission Code
    public static final int REQUEST_CODE_PERMISSION_READ_SMS_AND_CONTACT = 100;

    //
    public static final String EXPRESS_COMPANY = "pref_key_express_company";
    public static final String EXPRESS_DISAMBIGUATION = "pref_key_express_disambiguation";


    //RE

    public static final String RE_CAPTCHA_KEYWORD = "激活码|动态码|校验码|验证码|确认码|检验码|验证代码|激活代码|校验代码|动态代码|检验代码|确认代码|短信口令|动态密码|交易码|驗證碼|激活碼|動態碼|校驗碼|檢驗碼|驗證代碼|激活代碼|校驗代碼|確認代碼|動態代碼|檢驗代碼|上网密码";
    public static final String RE_DATA_KEYWORD = "总流量|套餐内剩余流量|结转流量|结转剩余流量";
    public static final String RE_EXPRESS_COMPANY_KEYWORD = "其他|圆通|中通|顺丰|韵达|百世|天天";
    public static final String RE_EXPRESS_DISAMBIGUATION = "自提";



}
