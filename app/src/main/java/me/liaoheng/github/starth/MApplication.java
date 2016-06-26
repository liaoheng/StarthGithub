package me.liaoheng.github.starth;

import android.app.Application;
import android.util.AndroidRuntimeException;
import android.util.Log;
import com.github.liaoheng.common.plus.CommonPlus;
import me.liaoheng.github.starth.model.UserLogin;
import me.liaoheng.github.starth.util.Constants;
import me.liaoheng.github.starth.util.RetrofitUtils;

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
            CommonPlus.IMAGE_DISK_CACHE_SIZE = Constants.IMAGE_DISK_CACHE_SIZE;
            CommonPlus.init2(this, Constants.PROJECT_NAME, BuildConfig.DEBUG);
            UserLogin.get().readLoginInfo();
            RetrofitUtils.get().init();
        } catch (final Exception e) {
            throw new AndroidRuntimeException(e);
        }
    }
}
