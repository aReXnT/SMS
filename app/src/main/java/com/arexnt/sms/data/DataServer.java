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
import com.arexnt.sms.common.SettingFragment;
import com.arexnt.sms.utils.DateFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.arexnt.sms.common.SettingFragment.ADDRESSES_CONTENT_PROVIDER;
import static com.arexnt.sms.common.SettingFragment.CONVERSATIONS_CONTENT_PROVIDER;
import static com.arexnt.sms.common.SettingFragment.DEFAULT_SORT_ORDER;

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
    private SparseArray<String> AddrRow;

    public DataServer(Context context, SharedPreferences preferences, String type){
        mContext = context;
        mPreferences = preferences;
        mConversationType = type;
    }
    /**
     * get Conversation List and filter by given.

     */
    public  List<Conversation> getConversation(){
        List<Conversation> list = new ArrayList<>();
        AddrRow = new SparseArray<>();
        getAddress();
        filterConversation();
        BlockedConversationHelper helper = new BlockedConversationHelper(null);
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor mCursor = null;
        switch (mConversationType){
            case SettingFragment.PERSONAL_LIST:
                mCursor = contentResolver
                        .query( CONVERSATIONS_CONTENT_PROVIDER, ALL_THREADS_PROJECTION,
                                helper.getCursorSelection(mPreferences, false),
                                helper.getPersonalConversationArray(mPreferences),
                                DEFAULT_SORT_ORDER);
                break;
            case SettingFragment.NOTIF_LIST:
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
                    String addr = AddrRow.get((int)item.getRecipient_id());
                    String name = getName(addr);
                    item.setName(name);
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
                    AddrRow.put(addrId, addr);
                }while (AddrCursor.moveToNext());
            }

            AddrCursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("AddrList",AddrRow.toString());
//        int mConversationHashCode = AddrRow.hashCode();
//        Log.d("ConversationHashCode", String.valueOf(mConversationHashCode));
    }

    private void filterConversation(){
        Set<String> mNotifList = mPreferences.getStringSet(SettingFragment.NOTIF_SENDERS, new HashSet<String>());
        Set<String> mPersonalList = mPreferences.getStringSet(SettingFragment.PERSONAL_SENDERS, new HashSet<String>());
        Set<String> newNotifList = new HashSet<>(mNotifList);
        Set<String> newPersonalList = new HashSet<>(mPersonalList);

        String pattern = "((^106)+?|(^10010)+?|(^10086)+?).*$";

        for(int i = 0; i < AddrRow.size(); i++){
            int id = AddrRow.keyAt(i);
            String addr = AddrRow.get(id);
            if (Pattern.matches(pattern,addr)){
                if (!newNotifList.contains(String.valueOf(id))){
                    newNotifList.add(String.valueOf(id));
                }
            }else {
                if (!newPersonalList.contains(String.valueOf(id))){
                    newPersonalList.add(String.valueOf(id));
                }
            }
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.putStringSet(SettingFragment.NOTIF_SENDERS, newNotifList).commit();
        editor.putStringSet(SettingFragment.PERSONAL_SENDERS, newPersonalList).commit();
    }


}
