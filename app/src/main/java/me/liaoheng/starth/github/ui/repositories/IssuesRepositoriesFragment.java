package me.liaoheng.starth.github.ui.repositories;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.Utils;
import com.github.liaoheng.common.util.ValidateUtils;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.BaseAdapter;
import me.liaoheng.starth.github.data.Page;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Issues;
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
 * @version 2016-09-05 15:28
 */
public class IssuesRepositoriesFragment extends LazyFragment {

    public static Fragment newInstance(Repositories repositories) {
        IssuesRepositoriesFragment fragment = new IssuesRepositoriesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.REPOSITORIES, repositories);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerViewHelper mRecyclerViewHelper;
    Repositories       mRepositories;
    Page mPage = new Page();
    IssuesRepositoriesAdapter mAdapter;

    @BindView(R.id.repositories_detail_issues_reload) Button mReload;

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_issues_list);
        ButterKnife.bind(this, getContentView());

        mRepositories = (Repositories) getArguments().getSerializable(Constants.REPOSITORIES);
        if (mRepositories == null) {
            return;
        }
        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity(), getContentView())
                .setLayoutManager()
                .setLoadMoreAndRefreshListener(new RecyclerViewHelper.RefreshListener() {
                    @Override public void onRefresh() {
                        loadData(mPage.refresh(), Page.PageState.REFRESH);
                    }
                }, new RecyclerViewHelper.LoadMoreListener() {
                    @Override public void onLoadMore() {
                        loadData(mPage.more(), Page.PageState.MORE);
                    }
                }).addLoadMoreFooterView().build();
        mAdapter = new IssuesRepositoriesAdapter(getContext(), null);
        mRecyclerViewHelper.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Issues>() {
            @Override public void onItemClick(Issues item, View view, int position) {
                WebViewActivity.start(getActivity(), item.getHtml_url());
            }
        });

        loadData(mPage, Page.PageState.ONE);
    }

    public void loadData(final Page page, final Page.PageState state) {
        Observable<Response<List<Issues>>> observable = NetworkClient.get().getReposService()
                .getRepositoriesIssues(mRepositories.getOwner().getLogin(), mRepositories.getName(),
                        "all", page.getCurPage()).subscribeOn(Schedulers.io());

        Utils
                .addSubscribe(observable, new Callback.EmptyCallback<Response<List<Issues>>>() {
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

                    @Override public void onSuccess(Response<List<Issues>> listResponse) {
                        List<Issues> repositories = listResponse.body();
                        if (ValidateUtils.isItemEmpty(repositories)) {
                            UIUtils.viewVisible((View) mReload.getParent());
                            return;
                        }

                        UIUtils.viewGone((View) mReload.getParent());
                        NetworkClient.get().setPage(listResponse.headers(), page);

                        if (Page.PageState.isMoreData(state)) {
                            mRecyclerViewHelper.setHasLoadedAllItems(page.isLast());
                        }

                        if (Page.PageState.isRefreshUI(state)) {
                            mAdapter.addAll(0, repositories);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.setOldSize();
                            mAdapter.addAll(repositories);
                            mAdapter.itemInserted(mAdapter.getOldSize());
                        }
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

    public class IssuesRepositoriesAdapter
            extends BaseAdapter<Issues, IssuesRepositoriesViewHolder> {

        public IssuesRepositoriesAdapter(Context context, List<Issues> list) {
            super(context, list);
        }

        @Override public IssuesRepositoriesViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
            View view = inflate(R.layout.fragment_repositories_detail_issues_list_item, parent);
            return new IssuesRepositoriesViewHolder(view);
        }

        @Override public void onBindViewHolderItem(IssuesRepositoriesViewHolder holder,
                                                   Issues issues, int position) {
            holder.onHandle(issues,position);
        }
    }

    public class IssuesRepositoriesViewHolder extends BaseRecyclerViewHolder<Issues> {

        @BindView(R.id.repositories_detail_issues_list_item_avatar) ImageView avatar;
        @BindView(R.id.repositories_detail_issues_list_item_state)   TextView  state;
        @BindView(R.id.repositories_detail_issues_list_item_title)  TextView  title;
        @BindView(R.id.repositories_detail_issues_list_item_date)   TextView  date;

        public IssuesRepositoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override public void onHandle(final Issues item,int position) {
            Glide.with(getContext()).load(item.getUser().getAvatar_url()).into(avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    UserInfoActivity.start(getContext(), item.getUser());
                }
            });
            state.setText(item.getState().toString());
            if (item.getState().equals(Issues.state.open)) {
                state.setTextColor(ContextCompat.getColor(getContext(),android.R.color.holo_green_dark));
            }else{
                state.setTextColor(ContextCompat.getColor(getContext(),android.R.color.holo_red_dark));
            }
            title.setText(item.getTitle());
            date.setText(item.getCreated_at());
        }
    }

}
