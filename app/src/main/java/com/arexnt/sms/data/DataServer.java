package com.arexnt.sms.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony.Threads;
import android.util.Log;
import android.util.SparseArray;

import com.arexnt.sms.common.BlockedConversationHelper;
import com.arexnt.sms.common.Constant;
import com.arexnt.sms.utils.DateFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.arexnt.sms.common.Constant.ADDRESSES_CONTENT_PROVIDER;
import static com.arexnt.sms.common.Constant.CONVERSATIONS_CONTENT_PROVIDER;
import static com.arexnt.sms.common.Constant.DEFAULT_SORT_ORDER;

public class DataServer {
    private static final String TAG = "DataServer";
    public static final String[] ALL_THREADS_PROJECTION = {
            Threads._ID, Threads.DATE, Threads.MESSAGE_COUNT, Threads.RECIPIENT_IDS,
            Threads.SNIPPET, Threads.SNIPPET_CHARSET, Threads.READ, Threads.ERROR,
            Threads.HAS_ATTACHMENT
    };

    private Context mContext;
    private SharedPreferences mPreferences;
    private String mConversationType;
    private SparseArray<String> mAddrRow;

    public DataServer(Context context, SharedPreferences preferences, String type){
        mContext = context;
        mPreferences = preferences;
        mConversationType = type;
        mAddrRow = new SparseArray<>();
    }
    /**
     * get Conversation List and filter by given.

     */
    public  List<Conversation> getConversation(){
        List<Conversation> list = new ArrayList<>();
        mAddrRow = new SparseArray<>();
        getAddress();
//        filterConversation();
        BlockedConversationHelper helper = new BlockedConversationHelper(null);
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor mCursor = null;
        switch (mConversationType){
            case Constant.PERSONAL_LIST:
                mCursor = contentResolver
                        .query( CONVERSATIONS_CONTENT_PROVIDER, ALL_THREADS_PROJECTION,
                                helper.getCursorSelection(mPreferences, false),
                                helper.getPersonalConversationArray(mPreferences),
                                DEFAULT_SORT_ORDER);
                break;
            case Constant.NOTIF_LIST:
                mCursor = contentResolver
                        .query( CONVERSATIONS_CONTENT_PROVIDER, ALL_THREADS_PROJECTION,
                                helper.getCursorSelection(mPreferences, true),
                                helper.getNotifConversationArray(mPreferences),
                                DEFAULT_SORT_ORDER);
                break;
        }


        try{
            if(mCursor.moveToFirst()){
//                Log.d(TAG, "getConversation: Cursor is't closed? ;" + Boolean.toString(mCursor.moveToNext()));
                do{
                    Conversation item = new Conversation();
                    item.setID(mCursor.getLong(mCursor.getColumnIndex(Threads._ID)));
                    long date;
                    date = mCursor.getLong(mCursor.getColumnIndex(Threads.DATE));
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm");
//                    Date formatDate = new Date(date);
                    item.setDate(DateFormatter.getConversationTimestamp(mContext,date));
                    item.setMessageCount(mCursor.getInt(mCursor.getColumnIndex(Threads.MESSAGE_COUNT)));
                    item.setRecipient_id(mCursor.getLong(mCursor.getColumnIndex(Threads.RECIPIENT_IDS)));
                    String content = mCursor.getString(mCursor.getColumnIndex(Threads.SNIPPET));
                    if (content != null){
                        item.setSnippet(content);
                    }else {
                        continue;
                    }
                    item.setSnippet(mCursor.getString(mCursor.getColumnIndex(Threads.SNIPPET)));
                    int snippt_cs = mCursor.getInt(mCursor.getColumnIndexOrThrow(Threads.SNIPPET_CHARSET));
                    if (snippt_cs != 0){
                        continue;
                    }
                    item.setHasUnreadMessages(mCursor.getInt(mCursor.getColumnIndex(Threads.READ))!=0);
                    int hasAttachment = mCursor.getInt(mCursor.getColumnIndex(Threads.HAS_ATTACHMENT));
                    if(hasAttachment == 0){
                        item.setHasAttachment(false);
                    }else{
                        continue;
                    }

                    item.setChecked(false);
                    String addr = mAddrRow.get((int)item.getRecipient_id());

                    if(mConversationType.equals(Constant.PERSONAL_LIST)){
                        String name = getName(addr);
                        item.setName(name);
                    }else {
                        item.setName(addr);
                    }
                    item.setAddress(addr);
                    list.add(item);
                }while(mCursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }


    public String getName(String address){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor;
        String name = address;

        try {
            cursor = contentResolver.query(uri, new String[]{BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor.moveToNext())
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, "Failed to find name for address " + address);
            e.printStackTrace();
        }

        return name;
    }
    public void getAddress() {
        try{
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor AddrCursor = contentResolver.query(ADDRESSES_CONTENT_PROVIDER, null, null, null, null);

            if (AddrCursor.moveToFirst()){
                do{
                    int addrId = AddrCursor.getInt(AddrCursor.getColumnIndexOrThrow("_id"));
                    String addr = AddrCursor.getString(AddrCursor.getColumnIndexOrThrow("address"));
                    mAddrRow.put(addrId, addr);
                }while (AddrCursor.moveToNext());
            }

            AddrCursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("AddrList",mAddrRow.toString());
//        int mConversationHashCode = AddrRow.hashCode();
//        Log.d("ConversationHashCode", String.valueOf(mConversationHashCode));
    }

    public void filterConversation(){
        Set<String> mNotifList = mPreferences.getStringSet(Constant.NOTIF_SENDERS, new HashSet<String>());
        Set<String> mPersonalList = mPreferences.getStringSet(Constant.PERSONAL_SENDERS, new HashSet<String>());
        Set<String> newNotifList = new HashSet<>();
        Set<String> newPersonalList = new HashSet<>();

        String pattern = "((^106)+?|(^10010)+?|(^10086)+?).*$";

        for(int i = 0; i < mAddrRow.size(); i++){
            int id = mAddrRow.keyAt(i);
            String addr = mAddrRow.get(id);
            if (Pattern.matches(pattern,addr)){
                newNotifList.add(String.valueOf(id));
            }else {
                newPersonalList.add(String.valueOf(id));
            }
        }


        if (!newNotifList.equals(mNotifList)){
//            SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(SMSApp.getContext());
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.clear();
//            editor.putStringSet(Constant.NOTIF_SENDERS, newNotifList).apply();
            mPreferences.edit().putStringSet(Constant.NOTIF_SENDERS, newNotifList).commit();
        }
        if (!newPersonalList.equals(mPersonalList)){
//            SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(SMSApp.getContext());
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.clear();
//            editor.putStringSet(Constant.PERSONAL_SENDERS, newPersonalList).apply();
            mPreferences.edit().putStringSet(Constant.PERSONAL_SENDERS, newPersonalList).commit();
        }

    }

    public String isContainSP(){
        try{
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor mAddrCursor = contentResolver.query(ADDRESSES_CONTENT_PROVIDER, null,
                    "address=?", new String[]{"10010"}, null);
            String str = "";
            if (mAddrCursor.moveToFirst()){
                str = mAddrCursor.getString(mAddrCursor.getColumnIndex("_id"));
            }
            return str;


        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
