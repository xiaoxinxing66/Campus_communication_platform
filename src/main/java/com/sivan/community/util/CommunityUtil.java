package com.sivan.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 对密码进行MD5加密 并且加盐加密
    // hello -> abc123def456
    // hello + 3e4a8 -> abc123def456abc
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //使用Spring自带的工具进行加密。
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 设置获得JSON字符串的方法。
     * @param code
     * @param message
     * @param map
     * @return
     */
    public static String getJSONString(int code , String message , Map<String , Object> map){
        JSONObject json = new JSONObject();
        json.put("code" , code);
        json.put("msg" , message);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key , map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code, String message){
        return getJSONString(code , message , null);
    }
    public static String getJSONString(int code){
        return getJSONString(code , null , null);
    }
}
