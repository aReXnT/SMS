package com.arexnt.sms.ui.conversation;

import com.arexnt.sms.R;
import com.arexnt.sms.data.Conversation;
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
    }
}
