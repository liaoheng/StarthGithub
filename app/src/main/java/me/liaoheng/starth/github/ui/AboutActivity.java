package me.liaoheng.starth.github.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.liaoheng.common.adapter.core.RecyclerViewHelper;
import com.github.liaoheng.common.util.AppUtils;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.UIUtils;
import java.util.ArrayList;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.ui.base.BaseActivity;
import net.yslibrary.licenseadapter.LicenseAdapter;
import net.yslibrary.licenseadapter.LicenseEntry;
import net.yslibrary.licenseadapter.Licenses;

/**
 * About
 * @author liaoheng
 * @version 2016-06-25 23:24
 */
public class AboutActivity extends BaseActivity {

    public static void start(Context context) {
        UIUtils.startActivity(context, AboutActivity.class);
    }

    RecyclerViewHelper mRecyclerViewHelper;

    @BindView(R.id.about_version) TextView version;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initSlidrStatusBar();

        try {
            version.setText(AppUtils.getVersionInfo(getApplication()).versionName);
        } catch (SystemException e) {
            L.getToast().e(TAG, getApplicationContext(), e);
        }

        mRecyclerViewHelper = new RecyclerViewHelper.Builder(this).build();

        List<LicenseEntry> licenses = new ArrayList<>();

        licenses.add(Licenses.noContent("Android SDK", "Google Inc.",
                "https://developer.android.com/sdk/terms.html"));

        licenses.add(Licenses.fromGitHub("square/okhttp", Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("square/retrofit", Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("bumptech/glide", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("reactivex/rxjava", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("reactivex/rxandroid", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("JakeWharton/butterknife", Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("greenrobot/EventBus", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("trello/rxlifecycle", Licenses.FILE_NO_EXTENSION));
        licenses.add(
                Licenses.fromGitHub("wasabeef/glide-transformations", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("orhanobut/logger", Licenses.FILE_NO_EXTENSION));
        licenses.add(
                Licenses.fromGitHub("h6ah4i/android-tablayouthelper", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("r0adkll/Slidr", Licenses.FILE_MD));
        licenses.add(Licenses.fromGitHub("H07000223/FlycoSystemBar", Licenses.NAME_MIT,
                Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("yshrsmz/LicenseAdapter", Licenses.FILE_NO_EXTENSION));

        licenses.add(Licenses.fromGitHub("FasterXML/jackson", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("apache/commons-io", Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("JodaOrg/joda-time", Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("ShootrNetwork/kmnumbers", Licenses.FILE_NO_EXTENSION));

        LicenseAdapter adapter = new LicenseAdapter(licenses);
        mRecyclerViewHelper.getRecyclerView().setHasFixedSize(true);
        mRecyclerViewHelper.setAdapter(adapter);

        Licenses.load(licenses);
    }

    @OnClick(R.id.about_icon) void open() {
        WebViewActivity.start(this, "https://github.com/liaoheng/StarthGithub");
    }
}
