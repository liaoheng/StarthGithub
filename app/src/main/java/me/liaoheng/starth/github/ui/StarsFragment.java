package me.liaoheng.starth.github.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.github.liaoheng.common.adapter.base.IBaseAdapter;
import com.github.liaoheng.common.adapter.core.RecyclerViewHelper;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.Utils;
import com.github.liaoheng.common.util.ValidateUtils;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.StarsAdapter;
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
 * User star list
 * @author liaoheng
 * @version 2016-06-25 13:13
 */
public class StarsFragment extends LazyFragment {

    RecyclerViewHelper mRecyclerViewHelper;
    User               user;
    Page mPage = new Page();
    StarsAdapter mStarsAdapter;

    public static Fragment newInstance(User user) {
        Bundle args = new Bundle();
        StarsFragment fragment = new StarsFragment();
        args.putSerializable(Constants.USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_stars);
        user = (User) getArguments().getSerializable(Constants.USER);
        if (user == null) {
            L.getToast().e(TAG, getApplicationContext(), "user is null");
            return;
        }
        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity(), getContentView())
                .setLayoutManager()
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
                        mRecyclerViewHelper.loading();
                        load(mPage.more(), Page.PageState.MORE);
                    }
                }).build();
        mStarsAdapter = new StarsAdapter(getActivity(), null);
        mStarsAdapter.setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Repositories>() {
            @Override public void onItemClick(Repositories item, View view, int position) {
                RepositoriesDetailActivity.start(getActivity(), item);
            }
        });
        mRecyclerViewHelper.setAdapter(mStarsAdapter);

        load(mPage, Page.PageState.ONE);
    }

    private void load(final Page page, final Page.PageState state) {
        Observable<Response<List<Repositories>>> userStars = NetworkClient.get().getUserService()
                .getUserStars(user.getLogin(), page.getCurPage()).subscribeOn(Schedulers.io());
        Utils.addSubscribe(userStars,
                new Callback.EmptyCallback<Response<List<Repositories>>>() {

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

                    @Override public void onSuccess(Response<List<Repositories>> listResponse) {
                        List<Repositories> stars = listResponse.body();
                        if (ValidateUtils.isItemEmpty(stars)) {
                            return;
                        }
                        NetworkClient.get().setPage(listResponse.headers(), page);

                        if (Page.PageState.isMoreData(state)) {
                            mRecyclerViewHelper.setHasLoadedAllItems(page.isLast());
                        }

                        if (Page.PageState.isRefreshUI(state)) {
                            mStarsAdapter.addAll(0, stars);
                            mStarsAdapter.notifyDataSetChanged();
                        } else {
                            mStarsAdapter.setOldSize();
                            mStarsAdapter.addAll(stars);
                            mStarsAdapter.itemInserted(mStarsAdapter.getOldSize());
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }
}
