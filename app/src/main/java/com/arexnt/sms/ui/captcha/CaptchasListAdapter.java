package com.arexnt.sms.ui.captcha;

import com.arexnt.sms.R;
import com.arexnt.sms.common.Constant;
import com.arexnt.sms.model.Message;
import com.arexnt.sms.utils.StringUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by arexnt on 2017/4/29.
 */

public class CaptchasListAdapter extends BaseMultiItemQuickAdapter<Message, BaseViewHolder> {

    public CaptchasListAdapter(List<Message> messageList){
        super(messageList);
        addItemType(Constant.isCaptcha, R.layout.captcha_item);
        addItemType(Constant.isCaptcha_Sep, R.layout.captcha_separation);
    }

    @Override
    protected void convert(BaseViewHolder helper, Message item) {
        switch (helper.getItemViewType()){
            case Constant.isCaptcha:
                helper.setText(R.id.captcha_addr, item.getSender());
                helper.setText(R.id.captcha_date, item.getReceiveDate());
                helper.setText(R.id.captcha_content, item.getContent());

                if (item.getCompanyName() != null){
                    String companyName = item.getCompanyName();
                    // 四个字的名字 换行
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
                    helper.setText(R.id.avatar_tv, companyName);
//                    RandomColor randomColor = new RandomColor();
//                    int color = randomColor.randomColor();
//                    helper.setBackgroundColor(R.id.avatar_tv, color);

                }else {
                    helper.setText(R.id.avatar_tv, "...");
                }
                break;
            case Constant.isCaptcha_Sep:
                helper.setText(R.id.date_message_tv, item.getReceiveDate());

                break;

        }

    }

}
