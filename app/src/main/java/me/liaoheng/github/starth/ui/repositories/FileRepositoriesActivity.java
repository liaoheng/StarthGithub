package me.liaoheng.github.starth.ui.repositories;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.flyco.systembar.SystemBarHelper;
import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.UIUtils;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import java.io.IOException;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.data.net.NetworkClient;
import me.liaoheng.github.starth.model.RepositoriesFileContent;
import me.liaoheng.github.starth.ui.base.BaseActivity;
import me.liaoheng.github.starth.util.Constants;
import okhttp3.ResponseBody;
import org.apache.commons.io.FilenameUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author liaoheng
 * @version 2016-07-19 12:49
 */
public class FileRepositoriesActivity extends BaseActivity {

    public static void start(Context context, RepositoriesFileContent repositoriesFileContent) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.REPOSITORIES, repositoriesFileContent);
        UIUtils.startActivity(context, FileRepositoriesActivity.class, bundle);
    }

    @BindView(R.id.lcp_list_swipe_container)      SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.repositories_file_detail_text) TextView           mTextView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories_file_detail);
        initToolBar();
        ButterKnife.bind(this);
        SystemBarHelper
                .tintStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 0);
        Slidr.attach(this, new SlidrConfig.Builder().edge(true).build());

        RepositoriesFileContent repositoriesFileContent = (RepositoriesFileContent) getIntent()
                .getSerializableExtra(Constants.REPOSITORIES);
        if (repositoriesFileContent == null) {
            L.getToast().e(TAG, getApplicationContext(), "RepositoriesFileContent is null");
            finish();
            return;
        }
        setTitle(repositoriesFileContent.getName());

        Observable<ResponseBody> repositoriesObservable = NetworkClient.get().getReposService()
                .getRepositoriesFileContent(repositoriesFileContent.getUrl())
                .subscribeOn(Schedulers.io());

        OkHttp3Utils.get()
                .addSubscribe(repositoriesObservable, new Callback.EmptyCallback<ResponseBody>() {
                    @Override public void onPreExecute() {
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override public void run() {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                        });
                    }

                    @Override public void onPostExecute() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override public void onSuccess(ResponseBody o) {
                        try {
                            mTextView.setText(o.string());
                        } catch (IOException e) {
                            onError(new SystemException(e));
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });

    }
}
