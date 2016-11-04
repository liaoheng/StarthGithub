package me.liaoheng.starth.github.ui.repositories;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.Utils;
import java.io.IOException;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.core.MenuItemHelper;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.RepositoriesFileContent;
import me.liaoheng.starth.github.ui.base.BaseActivity;
import me.liaoheng.starth.github.util.Constants;
import me.liaoheng.starth.github.view.MarkdownAndCodeHighlightView;
import okhttp3.ResponseBody;
import org.apache.commons.io.FilenameUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author liaoheng
 * @version 2016-07-19 12:49
 */
public class FileDetailRepositoriesActivity extends BaseActivity {

    public static void start(Context context, RepositoriesFileContent repositoriesFileContent) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.REPOSITORIES, repositoriesFileContent);
        UIUtils.startActivity(context, FileDetailRepositoriesActivity.class, bundle);
    }

    @BindView(R.id.lcp_list_swipe_container)              SwipeRefreshLayout           mSwipeRefreshLayout;
    @BindView(R.id.repositories_file_detail_content_view) MarkdownAndCodeHighlightView mContentView;

    public  boolean                 markdown;
    private MenuItemHelper          mMenuItemHelper;
    private RepositoriesFileContent mFileContent;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories_file_detail);
        initToolBar();
        ButterKnife.bind(this);
        initSlidrStatusBar();

        mFileContent = (RepositoriesFileContent) getIntent()
                .getSerializableExtra(Constants.REPOSITORIES);
        if (mFileContent == null) {
            L.getToast().e(TAG, getApplicationContext(), "RepositoriesFileContent is null");
            finish();
            return;
        }
        setTitle(mFileContent.getName());

        String path = FilenameUtils.getPath(mFileContent.getUrl());
        mContentView.setBaseUrl(path);
        mMenuItemHelper = MenuItemHelper.with();

        String name = FilenameUtils.getExtension(mFileContent.getName());
        markdown = name.equalsIgnoreCase("md") || name.equalsIgnoreCase("markdown");
        load(mFileContent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                load(mFileContent);
            }
        });

    }

    private void load(RepositoriesFileContent repositoriesFileContent) {
        Observable<ResponseBody> repositoriesObservable;
        if (markdown) {
            repositoriesObservable = NetworkClient.get().getReposService()
                    .getRepositoriesFileContentHtml(repositoriesFileContent.getUrl());
        } else {
            repositoriesObservable = NetworkClient.get().getReposService()
                    .getRepositoriesFileContentRaw(repositoriesFileContent.getUrl());
        }

        Utils.addSubscribe(repositoriesObservable.subscribeOn(Schedulers.io()),
                new Callback.EmptyCallback<ResponseBody>() {
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
                            if (markdown) {
                                mContentView.loadMarkdownData(o.string());
                            } else {
                                mContentView.loadHighlightData(o.string());
                            }
                        } catch (IOException e) {
                            onError(new SystemException(e));
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuItemHelper.onWebOptionsItemSelected(this, item, mFileContent.getHtml_url(),
                mFileContent.getDownload_url()) || super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        mMenuItemHelper.onWebCreateOptionsMenu(getMenuInflater(), menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override protected void onResume() {
        mContentView.onResume();
        super.onResume();
    }

    @Override protected void onPause() {
        mContentView.onPause();
        super.onPause();
    }

    @Override protected void onDestroy() {
        mContentView.destroy();
        super.onDestroy();
    }
}
