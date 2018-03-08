package me.liaoheng.starth.github;

import android.app.Application;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.github.liaoheng.common.network.CommonNet;
import com.github.liaoheng.common.plus.CommonPlus;
import me.liaoheng.starth.github.model.UserLogin;
import me.liaoheng.starth.github.util.Constants;
import me.liaoheng.starth.github.util.RetrofitUtils;

/**
 * app init
 * @author liaoheng
 * @version 2016-06-24 19:21
 */
public class MApplication extends Application {
    private final String TAG = MApplication.class.getSimpleName();

    @Override public void onCreate() {
        super.onCreate();
        Log.d(TAG, Constants.PROJECT_NAME + " Application Init");
        try {
            CommonNet.IMAGE_DISK_CACHE_SIZE = Constants.IMAGE_DISK_CACHE_SIZE;
            CommonPlus.init(this, Constants.PROJECT_NAME, BuildConfig.DEBUG);
            UserLogin.get().readLoginInfo();
            RetrofitUtils.get().init();
        } catch (final Exception e) {
            throw new AndroidRuntimeException(e);
        }
    }
}
