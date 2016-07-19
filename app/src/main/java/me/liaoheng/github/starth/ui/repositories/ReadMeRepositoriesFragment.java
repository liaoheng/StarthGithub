package me.liaoheng.github.starth.ui.repositories;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import eu.fiskur.markdownview.MarkdownView;
import java.io.IOException;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.data.net.NetworkClient;
import me.liaoheng.github.starth.model.Repositories;
import me.liaoheng.github.starth.ui.base.LazyFragment;
import me.liaoheng.github.starth.util.Constants;
import okhttp3.ResponseBody;
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

    @BindView(R.id.repositories_detail_read_me_markdown_view) MarkdownView mMarkDownView;

    @BindView(R.id.lcp_list_swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_read_me);
        ButterKnife.bind(this, getContentView());

        final Repositories repositories = (Repositories) getArguments()
                .getSerializable(Constants.REPOSITORIES);

        load(repositories);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                load(repositories);
            }
        });

    }

    private void load(Repositories repositories) {
        Observable<ResponseBody> repositoriesObservable = NetworkClient.get().getReposService()
                .getRepositoriesReadMe(repositories.getOwner().getLogin(), repositories.getName())
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
                            mMarkDownView.showMarkdown(o.string());
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
