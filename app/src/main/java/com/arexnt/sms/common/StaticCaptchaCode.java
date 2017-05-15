package com.arexnt.sms.common;

/**
 * Created by arexnt on 2017/4/29.
 */

public interface StaticCaptchaCode {

    String[] CPATCHAS_KEYWORD = {"激活码", "动态码", "校验码", "验证码", "确认码", "检验码", "验证代码", "激活代码",
            "校验代码", "动态代码", "检验代码", "确认代码", "短信口令", "动态密码", "交易码", "驗證碼", "激活碼", "動態碼", "校驗碼", "檢驗碼", "驗證代碼",
            "激活代碼", "校驗代碼", "確認代碼", "動態代碼", "檢驗代碼", "上网密码"};
    String[] CPATCHAS_KEYWORD_EN = {"CODE", "code"};
    String ACTION_CLICK = "com.arexnt.sms.intent.action.NotificationClick";
    String[] DATA_KEYWORD = {"总流量", "套餐内剩余流量", "结转流量", "结转剩余流量"};
    String[] EXPRESS_KEYWORD = {"菜鸟驿站","圆通","中通","顺丰","韵达","百世","天天"};



}
