package com.arexnt.sms.ui.messagelist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.arexnt.sms.R;
import com.arexnt.sms.common.SettingFragment;
import com.arexnt.sms.model.Message;
import com.arexnt.sms.utils.DateFormatter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_THREAD_ID = "thread_id";
    public static final String ARG_ADDRESS = "address";
    public static final Uri MMS_SMS_CONTENT_PROVIDER = Uri.parse("content://sms/conversations/");
    public static final String[] PROJECTION = new String[]{
//            Telephony.MmsSms.TYPE_DISCRIMINATOR_COLUMN,
            BaseColumns._ID,
            Telephony.Sms.Conversations.THREAD_ID,
            // For SMS
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE
    };

    private static long mThreadId;
    private static String mAddr;
    @BindView(R.id.message_toolbar)
    Toolbar mMessageToolbar;
    @BindView(R.id.list_message)
    RecyclerView mListMessage;

    private MessageListAdapter mAdapter;
    private LinearLayoutManager mManager;
    private ActionBar mBar;


    public static void launch(Context context, long threadId, String addr) {
        Intent intent = new Intent(context, MessageListActivity.class);
        intent.putExtra(ARG_THREAD_ID, threadId);
        intent.putExtra(ARG_ADDRESS, addr);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);
        ButterKnife.bind(this);
        onNewIntent(getIntent());
        getLoaderManager().initLoader(SettingFragment.LOADER_MESSAGES, null, this);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mThreadId = intent.getLongExtra(ARG_THREAD_ID, -1);
        mAddr = intent.getStringExtra(ARG_ADDRESS);
        Log.d("MessageListActivity", "getThreadId:" + Long.toString(mThreadId));
    }

    private void initView() {
        mAdapter = new MessageListAdapter(null);
        mAdapter.openLoadAnimation();
        mListMessage.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this);
        mListMessage.setLayoutManager(mManager);
        mListMessage.setAdapter(mAdapter);
        setSupportActionBar(mMessageToolbar);
        mBar = getSupportActionBar();
        if (mBar != null){
            getSupportActionBar().setTitle(mAddr);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == SettingFragment.LOADER_MESSAGES) {
            CursorLoader loader = new CursorLoader(getApplicationContext(),
                    Uri.withAppendedPath(MMS_SMS_CONTENT_PROVIDER, String.valueOf(mThreadId)),
                    PROJECTION, null, null, "date ASC"
            );
            return loader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == SettingFragment.LOADER_MESSAGES) {
            List<Message> messageList = new ArrayList<>();
            if (data.moveToFirst()) {
                do{
                    Message message = new Message();
                    try {
                        String msgContent = new String(data.getString(data.getColumnIndexOrThrow("body")).getBytes(),"UTF-8");
                        message.setContent(msgContent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    long date = data.getLong(data.getColumnIndexOrThrow("date"));
                    message.setReceiveDate(DateFormatter.getMessageTimestamp(getApplicationContext(), date));
                    String type = data.getString(data.getColumnIndexOrThrow("type"));
                    if (type != null || !type.isEmpty()){
                        if (type.equals("1")){
                            message.setItemType(SettingFragment.isMessage_in);
                        }
                        if (type.equals("2")){
                            message.setItemType(SettingFragment.isMessage_out);
                        }
                    }else {
                        continue;
                    }
                    Log.d("messageType", type);
                    messageList.add(message);
                }while (data.moveToNext());
            }
            mAdapter.setNewData(messageList);
            mAdapter.notifyDataSetChanged();
            mListMessage.setAdapter(mAdapter);

            //测试用输出点开会话的每一条短信
//            for(int i=0;i<messageList.size();i++){
//                Message test = messageList.get(i);
//                int showtype = test.getItemType();
//                String date = test.getReceiveDate();
//                String content =test.getContent();
//                Log.d("typeRecord",String.valueOf(showtype) + "\n date: " + date + ",\n content: " + content + " \n threadID:"+mThreadId);
//            }
//            mManager.scrollToPosition(data.getCount()-1);
            mListMessage.scrollToPosition(mAdapter.getItemCount()-1);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
//        mAdapter.setEmptyView(R.layout.message_item_in);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
