package me.liaoheng.github.starth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.UIUtils;
import me.liaoheng.github.starth.model.UserLogin;
import me.liaoheng.github.starth.ui.base.BaseActivity;

/**
 * 启动Route
 * @author liaoheng
 * @version 2016-06-24 21:07
 */
public class LaunchActivity extends BaseActivity {
    public final static String IS_EXIT_EXTRA = "isExit";

    /**
     * 正常退出应用
     * @param context {@link Context}
     */
    public static void exitApp(Context context) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IS_EXIT_EXTRA, true);
        context.startActivity(intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isExit = getIntent().getBooleanExtra(IS_EXIT_EXTRA, false);
        if (isExit) {
            L.Log.d(TAG, "正常退出应用！");
            finish();
            return;
        }

        if (UserLogin.get().isLogin()) {
            UIUtils.startActivity(this, MainActivity.class);
        } else {
            UIUtils.startActivity(this, OAuthActivity.class);
        }
        finish();
    }
}
