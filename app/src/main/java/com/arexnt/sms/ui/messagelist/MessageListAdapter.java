package com.arexnt.sms.ui.messagelist;


import com.arexnt.sms.R;
import com.arexnt.sms.common.SettingFragment;
import com.arexnt.sms.model.Message;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public class MessageListAdapter extends BaseMultiItemQuickAdapter<Message, BaseViewHolder> {

    public MessageListAdapter(List<Message> messageList){
        super(messageList);
        addItemType(SettingFragment.isMessage_in, R.layout.message_item_in);
        addItemType(SettingFragment.isMessage_out, R.layout.message_item_out);
    }

    @Override
    protected void convert(BaseViewHolder helper, Message item) {
        switch (helper.getItemViewType()){
            case SettingFragment.isMessage_in:
                helper.setText(R.id.in_message_time, item.getReceiveDate());
                helper.setText(R.id.in_message_content, item.getContent());
                break;
            case SettingFragment.isMessage_out:
                if (!item.getReceiveDate().isEmpty()){
                    helper.setText(R.id.message_time, item.getReceiveDate());
                    helper.setText(R.id.message_content, item.getContent());
                }
        }
    }
}
