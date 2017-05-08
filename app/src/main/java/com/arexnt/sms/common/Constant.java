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
    public static final String FIRST_BOOT = "pref_key_first_boot";
    public static final String LATEST_LIST_HASH = "pref_key_latest_conversation_addr_list_hash_code";

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

    public static final int LOADER_MESSAGES = 1;
    public static final int LOADER_CONVERSATIONS = 0;
    //Permission Code
    public static final int REQUEST_CODE_PERMISSION_READ_SMS_AND_CONTACT = 100;



}
