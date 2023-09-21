package com.sivan.community.util;

/**
 * 生成RedisKey的工具
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    private static final  String PREFIX_UV = "uv";
    private static final  String PREFIX_DAU = "dau";

    private static final String PREFIX_POST = "post";

    // 某个实体的赞
    // like:entity:entityType:entityId
    // set(userId) 使用set存放实体的赞，value是userId【点赞者的id】，统计所有赞的数量，可以用set的方法统计。
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> 谁关注的哪个实体
    // zset(entityId,now) 使用zset存储，存储实体id和关注时间。 比如关注的是用户，就是用户id和关注用户时间。
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录验证码 owner：用户的临时凭证，仅作为一个标识作用。
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    //单日UV
    public static String getUvKey(String date){
        return PREFIX_UV + SPLIT + date;
    }
    //区间UV
    public static String getUvKey(String startData , String endDate){
        return PREFIX_UV + SPLIT + startData + SPLIT  +endDate;
    }

    //单日DAU
    public static String getDauKey(String date){
        return PREFIX_DAU + SPLIT + date;
    }
    //区间DAU
    public static String getDauKey(String startDate, String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }
    //统计帖子分数
    public static String getPostScore(){
        return PREFIX_POST + SPLIT + "score";
    }

}
