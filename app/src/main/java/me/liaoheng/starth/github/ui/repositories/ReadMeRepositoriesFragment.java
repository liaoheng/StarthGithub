package me.liaoheng.starth.github.ui.repositories;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.Utils;

import java.io.FilenameFilter;
import java.io.IOException;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Repositories;
import me.liaoheng.starth.github.ui.base.LazyFragment;
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
public class ReadMeRepositoriesFragment extends LazyFragment {

    public static Fragment newInstance(Repositories repositories) {
        ReadMeRepositoriesFragment fragment = new ReadMeRepositoriesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.REPOSITORIES, repositories);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.repositories_detail_read_me_markdown_view) MarkdownAndCodeHighlightView mMarkDownView;

    @BindView(R.id.lcp_list_swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_read_me);
        ButterKnife.bind(this, getContentView());

        final Repositories repositories = (Repositories) getArguments()
                .getSerializable(Constants.REPOSITORIES);
        if (repositories == null){
            return;
        }
        mMarkDownView.setBaseUrl(repositories.getUrl()+"/contents/");
        load(repositories);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                load(repositories);
            }
        });

    }

    private void load(final Repositories repositories) {
        Observable<ResponseBody> repositoriesObservable = NetworkClient.get().getReposService()
                .getRepositoriesReadMe(repositories.getOwner().getLogin(), repositories.getName())
                .subscribeOn(Schedulers.io());

       Utils
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
                            String markdown = o.string();
                            if (TextUtils.isEmpty(markdown)) {
                                markdown = "# " + repositories.getName();
                            }
                            mMarkDownView.loadMarkdownData(markdown);
                        } catch (IOException e) {
                            onError(new SystemException(e));
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

    @Override protected void onResumeLazy() {
        mMarkDownView.onResume();
        super.onResumeLazy();
    }

    @Override protected void onPauseLazy() {
        mMarkDownView.onPause();
        super.onPauseLazy();
    }

    @Override protected void onDestroyViewLazy() {
        mMarkDownView.destroy();
        super.onDestroyViewLazy();
    }
}
