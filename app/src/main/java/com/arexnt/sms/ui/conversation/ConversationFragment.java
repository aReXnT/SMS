package com.arexnt.sms.ui.conversation;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arexnt.sms.R;
import com.arexnt.sms.SMSApp;
import com.arexnt.sms.common.SettingFragment;
import com.arexnt.sms.data.Conversation;
import com.arexnt.sms.data.DataServer;
import com.arexnt.sms.ui.messagelist.MessageListActivity;

import java.util.List;

public class ConversationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "ConversationFragment";
    public static final String ARG = "FragmentType";
    private SharedPreferences mPreferences;
    private List<Conversation> mConversations;
    private ConversationListAdapter mAdapter;
    private Context mContext;
    private DataServer mDataServer;
    private RecyclerView mRecyclerView;
    private String mFragmentType;
    private View mDataHeader;
    private View mExpressHeader;


    public static ConversationFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG, type);
        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentType = getArguments().getString(ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_list, container, false);
        mDataHeader = inflater.inflate(R.layout.data_head_view, container ,false);
        mExpressHeader = inflater.inflate(R.layout.express_header_view, container ,false);
        mRecyclerView = (RecyclerView) view;
        mContext = SMSApp.getContext();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mDataServer = new DataServer(getContext(), mPreferences, mFragmentType);
        mConversations = mDataServer.getConversation();
        init();
        setHeader();
        return view;
    }

    public void init(){
        //set up Adapter
        mAdapter = new ConversationListAdapter(R.layout.conversation_item, mConversations);
        mAdapter.openLoadAnimation();
        mAdapter.setOnItemClickListener( (adapter, view, position )-> {
                Conversation conversation = (Conversation) adapter.getItem(position);
                long id = conversation.getID();
                String addr = conversation.getName();
//                Toast.makeText(mContext, Long.toString(id), Toast.LENGTH_LONG).show();
                MessageListActivity.launch(getContext(), id, addr);
        });

        //set up recyclerview
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setHeader(){
        if (mFragmentType == SettingFragment.NOTIF_LIST) {
            mAdapter.addHeaderView(mDataHeader);
            mAdapter.addHeaderView(mExpressHeader);
            mDataHeader.setOnLongClickListener(getLongClikeListener());
            mExpressHeader.setOnLongClickListener(getLongClikeListener());

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private View.OnLongClickListener getLongClikeListener(){

        return v -> {
            Snackbar.make(v,"移除该卡片？",Snackbar.LENGTH_LONG)
                    .setAction("确认", v1 -> mAdapter.removeHeaderView(v))
                    .show();

            Vibrator vibrator=(Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{0,50}, -1);
                return false;
            };

    }
    private View.OnClickListener getOnClikListener() {
        return v -> mAdapter.removeHeaderView(v);
    }






}