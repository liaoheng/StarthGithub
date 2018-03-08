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
import me.liaoheng.starth.github.adapter.FollowingAdapter;
import me.liaoheng.starth.github.data.Page;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Following;
import me.liaoheng.starth.github.model.User;
import me.liaoheng.starth.github.ui.base.LazyFragment;
import me.liaoheng.starth.github.util.Constants;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * User following list
 * @author liaoheng
 * @version 2016-06-25 19:43:08
 */
public class FollowingFragment extends LazyFragment {

    RecyclerViewHelper mRecyclerViewHelper;
    User               user;
    Page mPage = new Page();
    FollowingAdapter mFollowingAdapter;

    public static Fragment newInstance(User user) {
        Bundle args = new Bundle();
        FollowingFragment fragment = new FollowingFragment();
        args.putSerializable(Constants.USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_followers);
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
                        mRecyclerViewHelper.changeToLoadMoreLoading();
                        load(mPage.more(), Page.PageState.MORE);
                    }
                }).build();
        mFollowingAdapter = new FollowingAdapter(getActivity(), null);
        mFollowingAdapter.setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Following>() {
            @Override public void onItemClick(Following item, View view, int position) {
                UserInfoActivity.start(getActivity(), new User().setLogin(item.getLogin()));
            }
        });
        mRecyclerViewHelper.setAdapter(mFollowingAdapter);

        load(mPage, Page.PageState.ONE);
    }

    private void load(final Page page, final Page.PageState state) {
        Observable<Response<List<Following>>> userStars = NetworkClient.get().getUserService()
                .getUserFollowing(user.getLogin(), page.getCurPage()).subscribeOn(Schedulers.io());
        Utils
                .addSubscribe(userStars, new Callback.EmptyCallback<Response<List<Following>>>() {

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

                    @Override public void onSuccess(Response<List<Following>> listResponse) {
                        List<Following> followings = listResponse.body();
                        if (ValidateUtils.isItemEmpty(followings)) {
                            return;
                        }
                        NetworkClient.get().setPage(listResponse.headers(), page);

                        if (Page.PageState.isMoreData(state)) {
                            mRecyclerViewHelper.setLoadMoreHasLoadedAllItems(page.isLast());
                        }

                        if (Page.PageState.isRefreshUI(state)) {
                            mFollowingAdapter.addAll(0, followings);
                            mFollowingAdapter.notifyDataSetChanged();
                        } else {
                            mFollowingAdapter.setOldSize();
                            mFollowingAdapter.addAll(followings);
                            mFollowingAdapter.itemInserted(mFollowingAdapter.getOldSize());
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }
}
