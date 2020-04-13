package com.ganghuan.util;

public class RedisUtil {


    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return ConstantUtil.PREFIX_ENTITY_LIKE+ConstantUtil.SPLIT+entityType+ConstantUtil.SPLIT+entityId;
    }

    public static  String getUserLikeKey(int userId){
        return ConstantUtil.PREFIX_USER_LIKE+ConstantUtil.SPLIT +userId;
    }

    //关注关键词
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return ConstantUtil.PREFIX_FOLLOWEE+ConstantUtil.SPLIT+userId+ConstantUtil.SPLIT+entityType;
    }

    public static String getFollowerKey(int entityType,int entityId){
        return ConstantUtil.PREFIX_FOLLOWER+ConstantUtil.SPLIT+entityType+ConstantUtil.SPLIT+entityId;
    }

    public static String getKaptchaKey(String owner) {
        return ConstantUtil.PREFIX_KAPTCHA + ConstantUtil.SPLIT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return ConstantUtil.PREFIX_TICKET + ConstantUtil.SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return ConstantUtil.PREFIX_USER + ConstantUtil.SPLIT + userId;
    }

}
