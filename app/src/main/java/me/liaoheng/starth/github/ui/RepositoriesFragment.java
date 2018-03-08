package me.liaoheng.starth.github.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.github.liaoheng.common.adapter.base.IBaseAdapter;
import com.github.liaoheng.common.adapter.core.RecyclerViewHelper;
import com.github.liaoheng.common.network.OkHttp3Utils;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.Utils;
import com.github.liaoheng.common.util.ValidateUtils;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.RepositoriesAdapter;
import me.liaoheng.starth.github.data.Page;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Repositories;
import me.liaoheng.starth.github.model.User;
import me.liaoheng.starth.github.ui.base.LazyFragment;
import me.liaoheng.starth.github.ui.repositories.RepositoriesDetailActivity;
import me.liaoheng.starth.github.util.Constants;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * User repositories list
 * @author liaoheng
 * @version 2016-06-25 18:43:08
 */
public class RepositoriesFragment extends LazyFragment {

    RecyclerViewHelper mRecyclerViewHelper;
    User               user;
    Page mPage = new Page();
    RepositoriesAdapter mRepositoriesAdapter;

    public static Fragment newInstance(User user) {
        Bundle args = new Bundle();
        RepositoriesFragment fragment = new RepositoriesFragment();
        args.putSerializable(Constants.USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories);
        user = (User) getArguments().getSerializable(Constants.USER);
        if (user == null) {
            L.getToast().e(TAG, getApplicationContext(), "user is null");
            return;
        }
        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity(), getContentView())
                .setLoadMoreAndRefreshListener(new RecyclerViewHelper.RefreshListener() {
                    @Override public void onRefresh() {
                        load(mPage.refresh(), Page.PageState.REFRESH);
                    }
                }, new RecyclerViewHelper.LoadMoreListener() {
                    @Override public void onLoadMore() {
                        load(mPage.more(), Page.PageState.MORE);
                    }
                }).addLoadMoreFooterView()
                .setFooterLoadMoreListener(new RecyclerViewHelper.LoadMoreListener() {
                    @Override public void onLoadMore() {
                        mRecyclerViewHelper.changeToLoadMoreLoading();
                        load(mPage.more(), Page.PageState.MORE);
                    }
                }).build();
        mRepositoriesAdapter = new RepositoriesAdapter(getActivity());
        mRepositoriesAdapter
                .setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Repositories>() {
                    @Override public void onItemClick(Repositories item, View view, int position) {
                        RepositoriesDetailActivity.start(getActivity(), item);
                    }
                });
        mRecyclerViewHelper.setAdapter(mRepositoriesAdapter);

        load(mPage, Page.PageState.ONE);
    }

    private void load(final Page page, final Page.PageState state) {
        Observable<Response<List<Repositories>>> userStars = NetworkClient.get().getUserService()
                .getUserRepos(user.getLogin(), page.getCurPage()).subscribeOn(Schedulers.io());
       Utils.addSubscribe(userStars,
                new Callback.EmptyCallback<Response<List<Repositories>>>() {

                    @Override public void onPreExecute() {
                        if (Page.PageState.isRefreshUI(state)) {
                            mRecyclerViewHelper.setSwipeRefreshing(true);
                        } else {
                            mRecyclerViewHelper.setLoadMoreLoading(true);
                        }
                    }

                    @Override public void onPostExecute() {
                        if (Page.PageState.isRefreshUI(state)) {
                            mRecyclerViewHelper.setSwipeRefreshing(false);
                        } else {
                            mRecyclerViewHelper.setLoadMoreLoading(false);
                        }
                    }

                    @Override public void onSuccess(Response<List<Repositories>> listResponse) {
                        List<Repositories> repositories = listResponse.body();
                        if (ValidateUtils.isItemEmpty(repositories)) {
                            return;
                        }
                        NetworkClient.get().setPage(listResponse.headers(), page);

                        if (Page.PageState.isMoreData(state)) {
                            mRecyclerViewHelper.setLoadMoreHasLoadedAllItems(page.isLast());
                        }

                        if (Page.PageState.isRefreshUI(state)) {
                            mRepositoriesAdapter.addAll(0, repositories);
                            mRepositoriesAdapter.notifyDataSetChanged();
                        } else {
                            mRepositoriesAdapter.setOldSize();
                            mRepositoriesAdapter.addAll(repositories);
                            mRepositoriesAdapter.itemInserted(mRepositoriesAdapter.getOldSize());
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }
}
