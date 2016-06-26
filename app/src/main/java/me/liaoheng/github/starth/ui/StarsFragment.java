package me.liaoheng.github.starth.ui;

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
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.adapter.StarsAdapter;
import me.liaoheng.github.starth.data.Page;
import me.liaoheng.github.starth.data.net.NetworkClient;
import me.liaoheng.github.starth.model.Star;
import me.liaoheng.github.starth.model.User;
import me.liaoheng.github.starth.ui.base.LazyFragment;
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
        args.putSerializable(UserInfoActivity.USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_stars);
        user = (User) getArguments().getSerializable(UserInfoActivity.USER);
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
        mStarsAdapter.setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Star>() {
            @Override public void onItemClick(Star item, View view, int position) {
                WebViewActivity.start(getActivity(), item.getHtml_url());
            }
        });
        mRecyclerViewHelper.setAdapter(mStarsAdapter);

        load(mPage, Page.PageState.ONE);
    }

    private void load(final Page page, final Page.PageState state) {
        Observable<Response<List<Star>>> userStars = NetworkClient.get().getUserService()
                .getUserStars(user.getLogin(), page.getCurPage()).subscribeOn(Schedulers.io());
        OkHttp3Utils.get()
                .addSubscribe(userStars, new Callback.EmptyCallback<Response<List<Star>>>() {

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

                    @Override public void onSuccess(Response<List<Star>> listResponse) {
                        List<Star> stars = listResponse.body();
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
