package com.facade.edi.starter.util;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author typhoon
 * @since V1.0.0
 */
public class StringUtil {

    public static final String DEFAULT_DELIMITER = ",";

    public static final String emptyStr() {
        return "";
    }

    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        if(isEmpty(str)) {
            return true;
        }
        return str.trim().length() <= 0;
    }

    public static boolean isDouble(String str) {
        if(isBlank(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String toNullableString(Object object) {
        if(null == object) {
            return null;
        }
        return object.toString();
    }

    public static String toNullableLowerCase(String str) {
        if(null == str) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String toNullableUpperCase(String str) {
        if(null == str) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * 重复拼接字符串
     *
     * @param target target
     * @param times times
     * @return String
     */
    public static final String repeatStr(String target,int times) {
        if(isEmpty(target) || times <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i < times; i ++) {
            builder.append(target);
        }
        return builder.toString();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static final String concatWithSymbol(CharSequence symbol,String...target) {
        StringBuilder builder = new StringBuilder();
        for (String s : target) {
            builder.append(symbol)
                    .append(s);
        }
        return builder.substring(symbol.length());
    }

    public static boolean isNumeric(String cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static final String camel2Underline(String s) {
        if(isEmpty(s)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i < s.length();i ++) {
            if(Character.isLowerCase(s.charAt(i))) {
                builder.append(s.charAt(i));
                continue;
            }
            builder.append("_")
                    .append(Character.toLowerCase(s.charAt(i)));
        }
        return builder.toString();
    }

    public static final List<String> split2List(String target) {
        if(isEmpty(target)) {
            return Collections.emptyList();
        }
        String delimiter =  DEFAULT_DELIMITER;
        return split2List(target,delimiter);
    }

    public static final List<String> split2List(String target,String delimiter) {
        if(isEmpty(target)) {
            return Collections.emptyList();
        }
        delimiter = null == delimiter ? DEFAULT_DELIMITER : delimiter;
        StringTokenizer tokenizer = new StringTokenizer(target,delimiter);
        List<String> result = new ArrayList<>(tokenizer.countTokens());
        while(tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }

    /**
     * 1.先按照实际要截取的字节长度，复制一份字节数组
     *
     * 2.转换回字符串，计算字符长度resLen，并按这个长度截取原字符串
     *
     * 3.计算截取的字符串的字节数是否等于需求长度len，相等则直接返回，不相等，则在resLen的基础上减1再截取，则为需要的结果
     * String的substring是按字符长度截取，可能最后一个字符会是半个字符
     * 转回bytes后判断是否超过长度，是的话，向前再截取1位,防止最后最后一个byte为半个字符的无效字符,舍弃
     * @param str str
     * @param len length
     * @param charSet charset
     * @return String
     */
    public static final String subStrByBytes(String str, int len, String charSet) throws UnsupportedEncodingException {
        if (isEmpty(str)) {
            return str;
        }
        byte[] bt = str.getBytes(charSet);
        if (bt.length <= len){
            return str;
        }
        byte[] br = new byte[len];
        System.arraycopy(bt, 0, br, 0, len);
        String res = new String(br, charSet);
        int resLen = res.length();
        if (str.substring(0, resLen).getBytes(charSet).length > len) {
            res = str.substring(0, resLen - 1);
        }
        return res;
    }

    public static final List<String> divideStrByBytes(String str,int len, String charSet) throws UnsupportedEncodingException{
        List<String> strSection = new ArrayList<>();
        byte[] bt = str.getBytes(charSet);
        int strLen = bt.length;
        int startPos = 0;
        int startStrPos = 0;
        while (startPos < strLen) {
            Integer subSectionLen = len;
            if (strLen - startPos < len) {
                subSectionLen = strLen - startPos;
            }
            byte[] br = new byte[subSectionLen];
            System.arraycopy(bt, startPos, br, 0, subSectionLen);
            String res = new String(br, charSet);
            int resLen = res.length();
            if (str.substring(startStrPos, startStrPos + resLen).getBytes(charSet).length > len) {
                res = res.substring(0, resLen - 1);
            }
            startStrPos += res.length();
            strSection.add(res);
            startPos += res.getBytes(charSet).length;
        }
        return strSection;
    }

    public static final boolean isJsonString(String str) {
        if(isEmpty(str)) {
            return false;
        }
        return str.startsWith("{") && str.endsWith("}");
    }

    public static final boolean isNotJsonString(String str) {
        return !isJsonString(str);
    }

    public static final boolean containsStr(String origin,String str) {
        if(isEmpty(origin) || isEmpty(str)) {
            return false;
        }
        return origin.contains(str);
    }

    public static final String replaceAll(String origin,String regex,String replacement) {
        if(null == origin) {
            return null;
        }
        return origin.replaceAll(regex,replacement);

    }

    public static void main(String[] args) {

    }

}
