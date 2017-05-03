package com.arexnt.sms.ui.captcha;

import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arexnt.sms.R;
import com.arexnt.sms.common.SettingFragment;
import com.arexnt.sms.model.Message;
import com.arexnt.sms.utils.ClipboardUtils;
import com.arexnt.sms.utils.SmsUtils;
import com.arexnt.sms.utils.TaskUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class CaptchaListFragment extends Fragment implements CaptchasListAdapter.OnItemClickListener{

    RecyclerView mRecyclerView;
    private List<Message> mMessages;
    private int mCurrentCaptchasCount = 0;
    private CaptchasListAdapter mAdapter;
    private LinearLayoutManager mManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.captcha_list, container, false);
        mRecyclerView = (RecyclerView) view;
        getAllMessage();
        setAdapter();
        return view;
    }

    private void setAdapter() {
        mAdapter = new CaptchasListAdapter(mMessages);
        mAdapter.openLoadAnimation();
        mRecyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void getAllMessage() {
        TaskUtils.execute(
                new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        SmsUtils smsUtils = new SmsUtils(getContext());
                        if (mMessages != null) {
                            mMessages.clear();
                            mCurrentCaptchasCount = 0;
                        }
                        mMessages = smsUtils.getAllCaptchMessages();
                        if (mMessages != null && mMessages.size() != 0) {
                            mCurrentCaptchasCount = getMessageCount();
                        }
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        /*
                            && mMessages.size() != 0 应该去掉,即使没有验证码短信,也应该更新
                            不然用户从系统收件箱删除短信之后再回到应用点击会崩溃
                         */
                        if (mMessages != null) {
                            setAdapter();
                        }
                    }
                }
        );

    }

    private int getMessageCount() {
        int amount = 0;
        for (Message message : mMessages) {
            if (message.getItemType() == SettingFragment.isCaptcha) {
                amount += 1;
            }
        }
        return amount;
    }



    public void setAvatar(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.captcha_item, null);
        TextView mAvatar = (TextView)view.findViewById(R.id.avatar_tv);
        GradientDrawable drawable = (GradientDrawable) mAvatar.getBackground();
        drawable.setColor(getResources().getColor(R.color.md_indigo_700));
    }

    private String getBussiness() {
        List<String> companys = new ArrayList<>();
        String bussinessStr = "";
        List<Message> messages = DataSupport.select("companyName").find(Message.class);
        if (mMessages != null && mMessages.size() != 0) {
            for (Message message : mMessages) {
                messages.add(message);
            }
        }
        int mark = 0;
        for (Message message : messages) {
            if (message.getCompanyName() != null) {
                if (!isExist(message.getCompanyName(), companys)) {
                    if (mark != 0) {
                        bussinessStr += "\n";
                    }
                    companys.add(message.getCompanyName());
                    bussinessStr += message.getCompanyName();
                    mark += 1;
                }
            }
        }
        return bussinessStr;
    }

    private Boolean isExist(String company, List<String> companys) {
        Boolean exist = false;
        for (String c : companys) {
            if (c.equals(company)) {
                exist = true;
                break;
            }
        }
        return exist;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter.getItemViewType(position) != SettingFragment.isCaptcha_Sep){
            Message captcha = (Message) adapter.getItem(position);
            String captchaStr = captcha.getCaptchas();
            if(captchaStr != null ){
                ClipboardUtils.putTextIntoClipboard(getContext(), captchaStr);
                Snackbar.make(getView(),
                        String.format(getResources().getString(R.string.copy_captcha), captchaStr),
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
