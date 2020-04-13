package com.ganghuan.util;

public class ConstantUtil {
    /**
     * 默认状态的登录凭证的超时时间
     */
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;


    /**
     * 实体类型: 帖子
     */
    public static final int ENTITY_TYPE_USER = 0;

    /**

    /**
     * 实体类型: 帖子
     */
    public static final int ENTITY_TYPE_BLOG = 1;

    /**
     * 实体类型: 评论
     */
    public static final int ENTITY_TYPE_COMMENT = 2;


    public static final String SPLIT = ":";
    public static final String PREFIX_ENTITY_LIKE = "like:entity";

    public static final String PREFIX_ENTITY_View = "view:entity";

    public static final String PREFIX_USER_LIKE = "like:user";

    public static final String PREFIX_FOLLOWEE = "followee";
    public static final String PREFIX_FOLLOWER = "follower";

    public static final String PREFIX_KAPTCHA = "kaptcha";
    public static final String PREFIX_TICKET = "ticket";

    public static final String PREFIX_USER = "user";

    public static final String TOPIC_COMMENT = "comment";

    public static final String TOPIC_LIKE = "like";

    public static final String TOPIC_FOLLOW = "follow";

    public static final String PREFIX_HOTBLOG= "hot:blog";


    //----for redis keys
}
