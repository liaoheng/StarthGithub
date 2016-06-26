package me.liaoheng.github.starth.util;

/**
 * 规则常量
 *
 * @author liaoheng
 */
public interface Constants {

    String CLIENT_ID = "12c828ddccfe511f6235";

    String CLIENT_SECRET = "0a839cec4b40b4189e29b01824b26afdbbfc1aa4";

    /**
     * API基础URL
     */
    String BASE_API_URL = "https://api.github.com/";

    /**
     * 授权回调页
     */
    String REDIRECT_URL = "https://github.com/liaoheng";

    String OAUTH_URL = "https://github.com/login/oauth/authorize";

    String PROJECT_NAME          = "StarthGithub";
    int    IMAGE_DISK_CACHE_SIZE = 1024 * 1024 * 1024; // 1024MB
    int    HTTP_DISK_CACHE_SIZE  = 50 * 1024 * 1024;  // 50MB
}
