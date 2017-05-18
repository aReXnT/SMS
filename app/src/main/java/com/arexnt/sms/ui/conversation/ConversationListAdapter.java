package com.arexnt.sms.ui.conversation;

import com.arexnt.sms.R;
import com.arexnt.sms.data.Conversation;
import com.arexnt.sms.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public class ConversationListAdapter extends BaseQuickAdapter<Conversation, BaseViewHolder>{

    public ConversationListAdapter(int layoutResId, List<Conversation> list){
        super(layoutResId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Conversation item) {
        helper.setText(R.id.conversation_list_name, item.getName());
        helper.setText(R.id.conversation_list_snippet, item.getSnippet());
        helper.setText(R.id.conversation_list_data, item.getDate());
        if (item.getCompany() != null && !item.getCompany().isEmpty()){
            helper.setBackgroundRes(R.id.conversation_list_avatar, R.drawable.solid_circle);
            String companyName = item.getCompany();
            if(StringUtils.isContainsChinese(companyName) && companyName.length() == 4){
                String fourCharsName = "";
                for(int i = 0; i < companyName.length(); i++){
                    if(i == 2){
                        fourCharsName += "\n";
                    }
                    fourCharsName += companyName.charAt(i);
                }
                companyName = fourCharsName;
            }
            helper.setText(R.id.conversation_list_avatar, companyName);
        }

    }
}
