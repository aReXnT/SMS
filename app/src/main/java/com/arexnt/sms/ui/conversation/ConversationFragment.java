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
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arexnt.sms.R;
import com.arexnt.sms.SMSApp;
import com.arexnt.sms.common.Constant;
import com.arexnt.sms.common.DataMessageHelper;
import com.arexnt.sms.data.Conversation;
import com.arexnt.sms.data.DataServer;
import com.arexnt.sms.ui.messagelist.MessageListActivity;
import com.arexnt.sms.ui.setting.SettingFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.arexnt.sms.common.Constant.CONVERSATIONS_CONTENT_PROVIDER;
import static com.arexnt.sms.common.Constant.DEFAULT_SORT_ORDER;
import static com.arexnt.sms.data.DataServer.ALL_THREADS_PROJECTION;

public class ConversationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = "ConversationFragment";
    public static final String ARG = "FragmentType";
    public static final int mPersonalTag = 1;
    public static final int mNotifTag = 2;
    private SharedPreferences mPreferences;
    private List<Conversation> mConversations;
    private ConversationListAdapter mAdapter;
    private Context mContext;
    private DataServer mDataServer;
    private RecyclerView mRecyclerView;
    private String mFragmentType;
    private View mDataHeader;
    private View mExpressHeader;
    private View mEmptyView;
    private boolean mEnableDataHeader;
    private boolean mEnableExpressHeader;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ViewGroup mContainer;



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
        mDataHeader = inflater.inflate(R.layout.header_view_data, container ,false);
        mExpressHeader = inflater.inflate(R.layout.header_view_express, container ,false);
        mEmptyView = inflater.inflate(R.layout.conversation_empty, container, false);
        mRecyclerView = (RecyclerView) view;
        mContext = SMSApp.getContext();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        mDataServer = new DataServer(getContext(), mPreferences, mFragmentType);
//        mConversations = mDataServer.getConversation();
        init();
        setHeader();
        mContainer = container; //只限用在空列表
        mAdapter.setEmptyView(R.layout.loading_view, container);
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    public void init(){
        //set up Adapter
        mAdapter = new ConversationListAdapter(R.layout.conversation_item, null);
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


        mEnableDataHeader = mPreferences.getBoolean(SettingFragment.KEY_PREF_ENABLE_DATA_CARDVIEW, true);
        mEnableExpressHeader = mPreferences.getBoolean(SettingFragment.KEY_PREF_ENABLE_EXPRESS_CARDVIEW, true);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(SettingFragment.KEY_PREF_ENABLE_DATA_CARDVIEW))
                    mEnableDataHeader = sharedPreferences.getBoolean(SettingFragment.KEY_PREF_ENABLE_DATA_CARDVIEW, true);
                Log.d("StatusOfHeader", "DataCardView: " + String.valueOf(mEnableDataHeader));
                if (key.equals(SettingFragment.KEY_PREF_ENABLE_EXPRESS_CARDVIEW))
                    mEnableExpressHeader = sharedPreferences.getBoolean(SettingFragment.KEY_PREF_ENABLE_EXPRESS_CARDVIEW, true);
                Log.d("StatusOfHeader", "ExpressCardView: " + String.valueOf(mEnableExpressHeader));
                setHeader();
            }
        };
//        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void setDataCardView(){
        DataServer dataServer = new DataServer(getContext(), mPreferences, Constant.NOTIF_LIST);
        String testId = dataServer.isContainSP();
        if (testId == null || testId.isEmpty()){
            mAdapter.removeHeaderView(mDataHeader);
            return;
        }
        DataMessageHelper helper = new DataMessageHelper(getContext());
        ArrayList<String> dataMsgList = new ArrayList<String>(helper.getDataMessageFromId(testId)){};
        if (!dataMsgList.isEmpty()){
            TextView tv1 = (TextView) mDataHeader.findViewById(R.id.data1);
            TextView tv2 = (TextView) mDataHeader.findViewById(R.id.data2);
            TextView tv3 = (TextView) mDataHeader.findViewById(R.id.data3);
            TextView tv4 = (TextView) mDataHeader.findViewById(R.id.data4);
            TextView date = (TextView) mDataHeader.findViewById(R.id.data_header_date);
            LinearLayout section2 = (LinearLayout) mDataHeader.findViewById(R.id.section2);
            ArrayList<TextView> views = new ArrayList<>();
            views.add(tv1);
            views.add(tv2);
            views.add(tv3);
            views.add(tv4);
            views.add(date);
            if (dataMsgList.size() < 5)
                section2.setVisibility(View.GONE);
            for (int i=0;i<dataMsgList.size();i++){
                Log.d("dataCardViewDetail","i : "+ i +", data: "+dataMsgList.get(i));
                if (i==(dataMsgList.size())){
                    date.setText(dataMsgList.get(i));
                    break;
                }
                views.get(i).setText(dataMsgList.get(i));
            }

            mAdapter.addHeaderView(mDataHeader);
            mDataHeader.setOnLongClickListener(getCardLongClikeListener());

        }
        Log.d("setOfDataMsg", dataMsgList.toString());
    }

    public void setExprexxCardView(){
        mAdapter.addHeaderView(mExpressHeader);
        mExpressHeader.setOnLongClickListener(getCardLongClikeListener());
    }
    public void setHeader(){
        mAdapter.removeAllHeaderView();
        mAdapter.removeAllFooterView();
        if (mFragmentType == Constant.NOTIF_LIST && mEnableDataHeader)
            setDataCardView();

        if (mFragmentType == Constant.NOTIF_LIST && mEnableExpressHeader)
            setExprexxCardView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                CONVERSATIONS_CONTENT_PROVIDER,ALL_THREADS_PROJECTION,
                null, null, DEFAULT_SORT_ORDER
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDataServer = new DataServer(getContext(), mPreferences, mFragmentType);
        mDataServer.getAddress();
        mDataServer.filterConversation();
        mConversations = mDataServer.getConversation();
        if (mConversations.size() != 0){
            mAdapter.setNewData(mConversations);
        }else{
            mAdapter.setEmptyView(mEmptyView);
        }

//        mAdapter.notifyDataSetChanged();
//        mRecyclerView.setAdapter(mAdapter);
        setHeader();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        Date curDate = new Date(System.currentTimeMillis());
        String time = dateFormat.format(curDate);
        Log.d("end runing time: ",time);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDataServer = null;

    }

    private View.OnLongClickListener getCardLongClikeListener(){

        return v -> {
            Snackbar.make(v,"移除该卡片？",Snackbar.LENGTH_LONG)
                    .setAction("确认", v1 -> mAdapter.removeHeaderView(v))
                    .show();

            Vibrator vibrator=(Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{0,50}, -1);
                return false;
            };

    }

    private View.OnClickListener getCardOnClikListener() {
        return v -> mAdapter.removeHeaderView(v);
    }

    public void scrollToTop(){
        mRecyclerView.scrollToPosition(0);
    }

    public void smoothScrollToTop(){
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
