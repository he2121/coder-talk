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

    /**
     * s文件上传路径
     */
    public static final String UPLOAD_PATH = "D:/project/upload";

    /**
     * 网站所在域
     */
    public static final String DOMAIN= "http://localhost:8080";
}
