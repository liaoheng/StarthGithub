package me.liaoheng.github.starth.ui.repositories;

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
import com.github.liaoheng.common.plus.adapter.BaseRecyclerAdapter;
import com.github.liaoheng.common.plus.adapter.IBaseAdapter;
import com.github.liaoheng.common.plus.adapter.holder.BaseRecyclerViewHolder;
import com.github.liaoheng.common.plus.core.RecyclerViewHelper;
import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import java.util.List;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.data.net.NetworkClient;
import me.liaoheng.github.starth.model.Commits;
import me.liaoheng.github.starth.model.Repositories;
import me.liaoheng.github.starth.ui.UserInfoActivity;
import me.liaoheng.github.starth.ui.base.LazyFragment;
import me.liaoheng.github.starth.util.Constants;
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

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_commits_list);

        Repositories repositories = (Repositories) getArguments()
                .getSerializable(Constants.REPOSITORIES);
        if (repositories == null) {
            return;
        }

        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity(), getContentView())
                .setLayoutManager().build();
        mCommitsRepositoriesAdapter = new CommitsRepositoriesAdapter(getActivity(), null);
        mRecyclerViewHelper.setAdapter(mCommitsRepositoriesAdapter);
        mCommitsRepositoriesAdapter
                .setOnItemClickListener(new IBaseAdapter.OnItemClickListener<Commits>() {
                    @Override public void onItemClick(Commits item, View view, int position) {

                    }
                });

        loadPath(repositories);
    }

    public class CommitsRepositoriesAdapter
            extends BaseRecyclerAdapter<Commits, CommitsRepositoriesViewHolder> {

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
            holder.onHandle(commits);
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

        @Override public void onHandle(final Commits item) {
            Glide.with(getContext()).load(item.getCommitter().getAvatar_url()).into(avatar);
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

    private void loadPath(Repositories repositories) {
        Observable<List<Commits>> repositoriesObservable = NetworkClient.get().getReposService()
                .getRepositoriesCommits(repositories.getOwner().getLogin(), repositories.getName())
                .subscribeOn(Schedulers.io());
        OkHttp3Utils.get()
                .addSubscribe(repositoriesObservable, new Callback.EmptyCallback<List<Commits>>() {
                    @Override public void onPreExecute() {
                        mRecyclerViewHelper.setRefreshCallback(true);
                        mRecyclerViewHelper.getRecyclerView().setEnabled(false);
                    }

                    @Override public void onPostExecute() {
                        mRecyclerViewHelper.setRefreshCallback(false);
                        mRecyclerViewHelper.getRecyclerView().setEnabled(true);
                    }

                    @Override public void onSuccess(List<Commits> o) {
                        mCommitsRepositoriesAdapter.update(o);
                        mCommitsRepositoriesAdapter.notifyDataSetChanged();
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

}
