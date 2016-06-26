package me.liaoheng.github.starth.model;

import android.text.TextUtils;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.PreferencesUtils;

/**
 * 登录用户信息
 * @author liaoheng
 * @version 2015-12-24 10:45
 */
public class UserLogin {
    private final       String TAG                     = UserLogin.class.getSimpleName();
    public final static String PreferenceFileName      = "user_login";
    public final static String PreferenceUserId        = "user_id";
    public final static String PreferenceUserName      = "user_name";
    public final static String PreferenceUserLoginName = "user_login_name";
    public final static String PreferenceUserAvatarUrl = "user_avatar_url";
    public final static String PreferenceAccessToken   = "access_token";

    private static UserLogin curUser;

    public static UserLogin get() {
        if (curUser == null) {
            curUser = new UserLogin();
        }
        return curUser;
    }

    private boolean login;
    private String  id;
    private String  name;
    private String  loginName;
    private String  avatar;
    private String  accessToken;

    private UserLogin() {
    }

    public void login(String id, String name, String loginName, String avatar, String accessToken) {
        this.login = true;
        this.id = id;
        this.name = name;
        this.loginName = loginName;
        this.avatar = avatar;
        this.accessToken = accessToken;
    }

    public void logout() {
        clear();
    }

    public void clear() {
        L.d(TAG, "clear : %s ", this);
        setAvatar(null).setId(null).setName(null).setLoginName(null).setLogin(false)
                .setAccessToken(null);
        PreferencesUtils.from(PreferenceFileName).clear();
    }

    public void readLoginInfo() {
        PreferencesUtils loginUser = PreferencesUtils.from(PreferenceFileName);
        this.accessToken = loginUser.getString(UserLogin.PreferenceAccessToken);
        this.login = !TextUtils.isEmpty(accessToken);
        if (!login) {
            return;
        }
        this.id = loginUser.getString(UserLogin.PreferenceUserId);
        this.name = loginUser.getString(UserLogin.PreferenceUserName);
        this.loginName = loginUser.getString(UserLogin.PreferenceUserLoginName);
        this.avatar = loginUser.getString(UserLogin.PreferenceUserAvatarUrl);
        L.d(TAG, "readLoginInfo : %s", this);
    }

    public void saveLoginInfo() {
        PreferencesUtils.from(UserLogin.PreferenceFileName).put(UserLogin.PreferenceUserName, name)
                .put(UserLogin.PreferenceAccessToken, accessToken)
                .put(UserLogin.PreferenceUserLoginName, loginName)
                .put(UserLogin.PreferenceUserAvatarUrl, avatar).put(UserLogin.PreferenceUserId, id)
                .apply();
        L.d(TAG, "saveLoginInfo : %s", this);
    }

    public User getUser() {
        User user = new User();
        user.setId(getIntId());
        user.setName(getName());
        user.setLogin(getLoginName());
        user.setAvatar_url(getAvatar());
        return user;
    }

    public boolean isLogin() {
        return login;
    }

    public UserLogin setLogin(boolean login) {
        this.login = login;
        return this;
    }

    public String getId() {
        return id;
    }

    public int getIntId() {
        return TextUtils.isEmpty(id) ? 0 : Integer.parseInt(id);
    }

    public UserLogin setId(String id) {
        this.id = id;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UserLogin setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getAccessTokenHeaderKey() {
        return "Authorization";
    }

    public String getAccessTokenHeaderContent() {
        return "token " + accessToken;
    }

    public String getLoginName() {
        return loginName;
    }

    public UserLogin setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserLogin setName(String name) {
        this.name = name;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserLogin setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    @Override public String toString() {
        return "UserLogin{" +
               "login=" + login +
               ", id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", loginName='" + loginName + '\'' +
               ", avatar='" + avatar + '\'' +
               ", accessToken='" + accessToken + '\'' +
               '}';
    }
}
