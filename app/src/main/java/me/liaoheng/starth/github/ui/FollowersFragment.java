package me.liaoheng.starth.github.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.github.liaoheng.common.plus.adapter.IBaseAdapter;
import com.github.liaoheng.common.plus.core.RecyclerViewHelper;
import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.ValidateUtils;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.FollowersAdapter;
import me.liaoheng.starth.github.data.Page;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Followers;
import me.liaoheng.starth.github.model.User;
import me.liaoheng.starth.github.ui.base.LazyFragment;
import me.liaoheng.starth.github.util.Constants;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * User followers list
 * @author liaoheng
 * @version 2016-06-25 19:43:08
 */
public class FollowersFragment extends LazyFragment {

    RecyclerViewHelper mRecyclerViewHelper;
    User               user;
    Page mPage = new Page();
    FollowersAdapter mFollowersAdapter;

    public static Fragment newInstance(User user) {
        Bundle args = new Bundle();
        FollowersFragment fragment = new FollowersFragment();
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
                        mRecyclerViewHelper.loading();
                        load(mPage.more(), Page.PageState.MORE);
                    }
                }).build();
        mFollowersAdapter = new FollowersAdapter(getActivity(), null);
        mFollowersAdapter.setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Followers>() {
            @Override public void onItemClick(Followers item, View view, int position) {
                UserInfoActivity.start(getActivity(), new User().setLogin(item.getLogin()));
            }
        });
        mRecyclerViewHelper.setAdapter(mFollowersAdapter);

        load(mPage, Page.PageState.ONE);
    }

    private void load(final Page page, final Page.PageState state) {
        Observable<Response<List<Followers>>> userStars = NetworkClient.get().getUserService()
                .getUserFollowers(user.getLogin(), page.getCurPage()).subscribeOn(Schedulers.io());
        OkHttp3Utils.get()
                .addSubscribe(userStars, new Callback.EmptyCallback<Response<List<Followers>>>() {

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

                    @Override public void onSuccess(Response<List<Followers>> listResponse) {
                        List<Followers> followerses = listResponse.body();
                        if (ValidateUtils.isItemEmpty(followerses)) {
                            return;
                        }
                        NetworkClient.get().setPage(listResponse.headers(), page);

                        if (Page.PageState.isMoreData(state)) {
                            mRecyclerViewHelper.setHasLoadedAllItems(page.isLast());
                        }

                        if (Page.PageState.isRefreshUI(state)) {
                            mFollowersAdapter.addAll(0, followerses);
                            mFollowersAdapter.notifyDataSetChanged();
                        } else {
                            mFollowersAdapter.setOldSize();
                            mFollowersAdapter.addAll(followerses);
                            mFollowersAdapter.itemInserted(mFollowersAdapter.getOldSize());
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }
}
