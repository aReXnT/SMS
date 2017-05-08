package com.arexnt.sms.utils;


import com.arexnt.sms.model.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arexnt.sms.StaticCaptchaCode.CPATCHAS_KEYWORD;
import static com.arexnt.sms.StaticCaptchaCode.CPATCHAS_KEYWORD_EN;
import static com.arexnt.sms.StaticCaptchaCode.DATA_KEYWORD;

public class StringUtils {

    private StringUtils() {}

    /**
     * 判断字符串中子字符串出现次数
     *
     * @param str
     * @param key
     * @return
     */
    public static int getSubCount(String str, String key) {
        int count = 0;
        int index = 0;
        String strOperation = str;
        while (index != -1) {
            index = strOperation.indexOf(key);
            if (index == -1) break;
            int length = key.length();
            strOperation = strOperation.substring(index + length);
            count++;
        }
        return count;
    }

    public static String getContentInBracket(String str, String address) {
        Pattern pattern = Pattern.compile("\\【(.*?)\\】");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            if (matcher.group(1) != null && matcher.group(1).length() < 6) {

                return analyseSpecialCompany(matcher.group(1), str, address);
            }
        }
        Pattern pattern1 = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher1 = pattern1.matcher(str);
        while (matcher1.find()) {
            if (matcher1.group(1) != null && matcher1.group(1).length() < 6) {

                return analyseSpecialCompany(matcher1.group(1), str, address);
            }
        }
        Pattern pattern2 = Pattern.compile("\\((.*?)\\)");
        Matcher matcher2 = pattern2.matcher(str);
        while (matcher2.find()) {
            if (matcher2.group(1) != null && matcher2.group(1).length() < 6) {

                return analyseSpecialCompany(matcher2.group(1), str, address);
            }
        }
        return null;
    }

    private static String analyseSpecialCompany(String company, String content, String address) {
        String companyName = company;
        if (company.equals("掌淘科技")) {
            int index = content.indexOf("的验证码");
            companyName = content.substring(0, index);
            companyName = companyName.replaceAll("【掌淘科技】", "").trim();
        } else {
            if (content.contains("贝壳单词的验证码")) {
                companyName = "贝壳单词";
            }
        }
        if (address.equals("10010")) {
            companyName = "中国联通";
        }
        if (address.equals("10086")) {
            companyName = "中国移动";
        }
        if (address.equals("10000")) {
            companyName = "中国电信";
        }
        return companyName;
    }

    /**
     * 判断字符串中时否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainsChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg || str.contains("【") || str.contains("】") || str.contains("。");
    }

    public static boolean isPersonalMoblieNO(String mobiles) {
        if(mobiles != null) {
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            if (m == null) {
                return false;
            }
            else {
                return m.matches();
            }
        }
        return false;
    }

    public static String tryToGetCaptchas(String str) {
        Pattern continuousNumberPattern = Pattern.compile("[a-zA-Z0-9\\.]+");
        Matcher m = continuousNumberPattern.matcher(str);
        String mostLikelyCaptchas = "";
        int currentLevel = -1; //只有字母相似级别为0， 只有字母和数字可能级别为1, 只有数字可能级别为2.
        while (m.find()) {
            if (m.group().length() > 3 && m.group().length() < 8 && !m.group().contains(".")) {
                if(isNearToKeyWord(m.group(), str)) {
                    final String strr = m.group();
                    if(currentLevel == -1) {
                        mostLikelyCaptchas = m.group();
                    }
                    final int level = getLikelyLevel(m.group());
                    if(level > currentLevel) {
                        mostLikelyCaptchas = m.group();
                    }
                    currentLevel = level;
                }
            }
        }
        return mostLikelyCaptchas;
    }

    public static String tryToGetCaptchasEn(String str) {
        Pattern continuousNumberPattern = Pattern.compile("[0-9\\.]+");
        Matcher m = continuousNumberPattern.matcher(str);
        while (m.find()) {
            if (m.group().length() > 3 && m.group().length() < 8 && !m.group().contains(".")) {
                if(isNearToKeyWordEn(m.group(), str)) {
                    return m.group();
                }
            }
        }
        return "";
    }

    private static  int getLikelyLevel(String str) {
        if(str.matches("^[0-9]*$")) {
            return 2;
        } else if(str.matches("^[a-zA-Z]*$")) {
            return 0;
        } else {
            return 1;
        }

    }

    public static boolean isNearToKeyWordEn(String currentStr, String content) {
        int startPosition = 0;
        int endPosition = content.length() - 1;
        if (content.indexOf(currentStr) > 12) {
            startPosition = content.indexOf(currentStr) - 12;
        }
        if (content.indexOf(currentStr)  + currentStr.length() + 12 < content.length() - 1) {
            endPosition = content.indexOf(currentStr) + currentStr.length() + 12;
        }
        Boolean isNearToKeyWord = false;
        for (int i = 0; i < CPATCHAS_KEYWORD_EN.length; i++) {
            if (content.substring(startPosition, endPosition).contains(CPATCHAS_KEYWORD_EN[i])) {
                isNearToKeyWord = true;
                break;
            }
        }
        return isNearToKeyWord;
    }

    public static boolean isNearToKeyWord(String currentStr, String content) {
        int startPosition = 0;
        int endPosition = content.length() - 1;
        if (content.indexOf(currentStr) > 12) {
            startPosition = content.indexOf(currentStr) - 12;
        }
        if (content.indexOf(currentStr)  + currentStr.length() + 12 < content.length() - 1) {
            endPosition = content.indexOf(currentStr) + currentStr.length() + 12;
        }
        Boolean isNearToKeyWord = false;
        for (int i = 0; i < CPATCHAS_KEYWORD.length; i++) {
            if (content.substring(startPosition, endPosition).contains(CPATCHAS_KEYWORD[i])) {
                isNearToKeyWord = true;
                break;
            }
        }
        return isNearToKeyWord;
    }

    public static boolean isCaptchasMessage(String content) {
        Boolean isCaptchasMessage = false;
        for (int i = 0; i < CPATCHAS_KEYWORD.length; i++) {
            if (content.contains(CPATCHAS_KEYWORD[i])) {
                isCaptchasMessage = true;
                break;
            }
        }
        return isCaptchasMessage;
    }

    public static boolean isCaptchasMessageEn(String content) {
        Boolean isCaptchasMessage = false;
        for (int i = 0; i < CPATCHAS_KEYWORD_EN.length; i++) {
            if (content.contains(CPATCHAS_KEYWORD_EN[i])) {
                isCaptchasMessage = true;
                break;
            }
        }
        return isCaptchasMessage;
    }

    /**
     * 根据短信获取描述文字
     *
     * @return
     */
    public static String getResultText(Message message, Boolean isNotificationText) {
        String resultStr = "";
        if (message.getCompanyName() != null && !isNotificationText) {
            resultStr += "来自" + message.getCompanyName() + "的验证码：";
        } else {
            resultStr += "当前验证码为：";
        }
        if (message.getCaptchas() != null) {
            resultStr += message.getCaptchas();
        } else {
            resultStr += "点击查看详情.";
        }
        return resultStr;
    }

    /**
     * @prama: str 要判断是否包含特殊字符的目标字符串
     */
    public static boolean compileExChar(String str) {

        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？ ]";

        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);

        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDataMessage(String content){
        Boolean isDataMessage = false;
        for (int i = 0; i < DATA_KEYWORD.length; i++) {
            if (content.contains(DATA_KEYWORD[i])) {
                isDataMessage = true;
                break;
            }
        }
        return isDataMessage;
    }


}
