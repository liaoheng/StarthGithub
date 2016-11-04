package me.liaoheng.starth.github.ui.repositories;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.liaoheng.common.adapter.base.IBaseAdapter;
import com.github.liaoheng.common.adapter.core.RecyclerViewHelper;
import com.github.liaoheng.common.adapter.holder.BaseRecyclerViewHolder;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.Utils;
import com.github.liaoheng.common.util.ValidateUtils;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.BaseAdapter;
import me.liaoheng.starth.github.data.Page;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Commits;
import me.liaoheng.starth.github.model.Repositories;
import me.liaoheng.starth.github.ui.UserInfoActivity;
import me.liaoheng.starth.github.ui.WebViewActivity;
import me.liaoheng.starth.github.ui.base.LazyFragment;
import me.liaoheng.starth.github.util.Constants;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author liaoheng
 * @version 2016-07-19 12:49
 */
public class CommitsRepositoriesFragment extends LazyFragment {

    public static Fragment newInstance(Repositories repositories) {
        CommitsRepositoriesFragment fragment = new CommitsRepositoriesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.REPOSITORIES, repositories);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerViewHelper         mRecyclerViewHelper;
    CommitsRepositoriesAdapter mCommitsRepositoriesAdapter;
    Page mPage = new Page();
    Repositories mRepositories;

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_commits_list);

        mRepositories = (Repositories) getArguments()
                .getSerializable(Constants.REPOSITORIES);
        if (mRepositories == null) {
            return;
        }

        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity(), getContentView())
                .setLayoutManager()
                .setLoadMoreAndRefreshListener(new RecyclerViewHelper.RefreshListener() {
                    @Override public void onRefresh() {
                        loadPath(mPage.refresh(), Page.PageState.REFRESH);
                    }
                }, new RecyclerViewHelper.LoadMoreListener() {
                    @Override public void onLoadMore() {
                        loadPath(mPage.more(), Page.PageState.MORE);
                    }
                }).addLoadMoreFooterView().build();
        mCommitsRepositoriesAdapter = new CommitsRepositoriesAdapter(getActivity(), null);
        mRecyclerViewHelper.setAdapter(mCommitsRepositoriesAdapter);
        mCommitsRepositoriesAdapter
                .setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Commits>() {
                    @Override public void onItemClick(Commits item, View view, int position) {
                        WebViewActivity.start(getActivity(), item.getHtml_url());
                    }
                });

        loadPath(mPage, Page.PageState.ONE);
    }

    public class CommitsRepositoriesAdapter
            extends BaseAdapter<Commits, CommitsRepositoriesViewHolder> {

        public CommitsRepositoriesAdapter(Context context, List<Commits> list) {
            super(context, list);
        }

        @Override public CommitsRepositoriesViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            View view = inflate(R.layout.fragment_repositories_detail_commits_list_item, parent,
                    false);
            return new CommitsRepositoriesViewHolder(view);
        }

        @Override public void onBindViewHolderItem(CommitsRepositoriesViewHolder holder,
                                                   Commits commits, int position) {
            holder.onHandle(commits,position);
        }
    }

    public class CommitsRepositoriesViewHolder extends BaseRecyclerViewHolder<Commits> {

        @BindView(R.id.repositories_detail_commits_list_item_commit_avatar)  ImageView avatar;
        @BindView(R.id.repositories_detail_commits_list_item_commit_name)    TextView  name;
        @BindView(R.id.repositories_detail_commits_list_item_commit_message) TextView  message;
        @BindView(R.id.repositories_detail_commits_list_item_commit_date)    TextView  date;

        public CommitsRepositoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override public void onHandle(final Commits item,int position) {
            if (item.getCommitter() != null) {
                Glide.with(getContext()).load(item.getCommitter().getAvatar_url()).into(avatar);
            }
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    UserInfoActivity.start(getContext(), item.getCommitter());
                }
            });
            name.setText(item.getCommit().getCommitter().getName());
            message.setText(item.getCommit().getMessage());
            date.setText(item.getCommit().getCommitter().getDate());
        }
    }

    private void loadPath(final Page page, final Page.PageState state) {
        Observable<Response<List<Commits>>> repositoriesObservable = NetworkClient.get()
                .getReposService().getRepositoriesCommits(mRepositories.getOwner().getLogin(),
                        mRepositories.getName(), page.getCurPage())
                .subscribeOn(Schedulers.io());
       Utils.addSubscribe(repositoriesObservable,
                new Callback.EmptyCallback<Response<List<Commits>>>() {
                    @Override public void onPreExecute() {
                        if (Page.PageState.isRefreshUI(state)) {
                            mRecyclerViewHelper.setRefreshCallback(true);
                        } else {
                            mRecyclerViewHelper.setLoading(true);
                        }
                    }

                    @Override public void onPostExecute() {
                        if (Page.PageState.isRefreshUI(state)) {
                            mRecyclerViewHelper.setRefreshCallback(false);
                        } else {
                            mRecyclerViewHelper.setLoading(false);
                        }
                    }

                    @Override public void onSuccess(Response<List<Commits>> listResponse) {

                        List<Commits> repositories = listResponse.body();
                        if (ValidateUtils.isItemEmpty(repositories)) {
                            return;
                        }
                        NetworkClient.get().setPage(listResponse.headers(), page);

                        if (Page.PageState.isMoreData(state)) {
                            mRecyclerViewHelper.setHasLoadedAllItems(page.isLast());
                        }

                        if (Page.PageState.isRefreshUI(state)) {
                            mCommitsRepositoriesAdapter.addAll(0, repositories);
                            mCommitsRepositoriesAdapter.notifyDataSetChanged();
                        } else {
                            mCommitsRepositoriesAdapter.setOldSize();
                            mCommitsRepositoriesAdapter.addAll(repositories);
                            mCommitsRepositoriesAdapter
                                    .itemInserted(mCommitsRepositoriesAdapter.getOldSize());
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

}
