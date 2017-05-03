package com.arexnt.sms.common;


import android.content.SharedPreferences;
import android.provider.Telephony;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlockedConversationHelper {

    private String mPreferencesKey;

    public BlockedConversationHelper(@Nullable String type){
        if (type != null){
            mPreferencesKey = type;
        }
    }

    public void blockConversation(SharedPreferences prefs, long threadId ){

        Set<String> idStrings = prefs.getStringSet(mPreferencesKey, new HashSet<String>());
        Set<String> newString = new HashSet<String>(idStrings);
        newString.add(String.valueOf(threadId));
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet(mPreferencesKey, newString).commit();
    }

    public String[] getPersonalConversationArray(SharedPreferences prefs ) {
        Set<String> idStrings = getPersonalConversations(prefs);
        return idStrings.toArray(new String[idStrings.size()]);
    }
    public String[] getNotifConversationArray(SharedPreferences prefs ) {
        Set<String> idStrings = getNotifConversations(prefs);
        return idStrings.toArray(new String[idStrings.size()]);
    }
    public String[] getBlockedConversationArray(SharedPreferences prefs ) {
        Set<String> idStrings = getBlockedConversations(prefs);
        return idStrings.toArray(new String[idStrings.size()]);
    }


    public Set<String> getPersonalConversations(SharedPreferences prefs ) {
        return prefs.getStringSet(SettingFragment.PERSONAL_SENDERS, new HashSet<String>());
    }

    public Set<String> getNotifConversations(SharedPreferences prefs ) {
        return prefs.getStringSet(SettingFragment.NOTIF_SENDERS, new HashSet<String>());
    }

    public Set<String> getBlockedConversations(SharedPreferences prefs ) {
        return prefs.getStringSet(SettingFragment.BLOCKED_SENDERS, new HashSet<String>());
    }



    public String getCursorSelection(SharedPreferences prefs, boolean blocked) {

        StringBuilder selection = new StringBuilder();
        selection.append(Telephony.Threads.MESSAGE_COUNT);
        selection.append(" != 0");
        selection.append(" AND ");
        selection.append(Telephony.Threads._ID);
//        if (!blocked) selection.append(" NOT");
        selection.append(" IN (");
        Set<String> idStrings = new HashSet<>();
        if (blocked){
            idStrings = getNotifConversations(prefs);
        }else{
            idStrings = getPersonalConversations(prefs);
        }
//        Log.d("idStringSize","size of idString: "+Integer.toString(idStrings.size()));
        for (int i = 0; i < idStrings.size(); i++) {
            selection.append("?");
            if (i < idStrings.size() - 1) {
                selection.append(",");
            }
        }
        selection.append(")");
        return selection.toString();
    }


}
