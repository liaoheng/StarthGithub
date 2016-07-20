package me.liaoheng.github.starth.ui.repositories;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import me.liaoheng.github.starth.model.Repositories;
import me.liaoheng.github.starth.model.RepositoriesFileContent;
import me.liaoheng.github.starth.ui.base.LazyFragment;
import me.liaoheng.github.starth.util.Constants;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author liaoheng
 * @version 2016-07-19 12:49
 */
public class FileRepositoriesFragment extends LazyFragment {

    public static Fragment newInstance(Repositories repositories) {
        FileRepositoriesFragment fragment = new FileRepositoriesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.REPOSITORIES, repositories);
        fragment.setArguments(args);
        return fragment;
    }

    Repositories            mRepositories;
    RecyclerViewHelper      mRecyclerViewHelper;
    FileRepositoriesAdapter mFileRepositoriesAdapter;

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_file_list);

        mRepositories = (Repositories) getArguments().getSerializable(Constants.REPOSITORIES);

        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity()).setLayoutManager()
                .build();
        mFileRepositoriesAdapter = new FileRepositoriesAdapter(getActivity(), null);
        mRecyclerViewHelper.setAdapter(mFileRepositoriesAdapter);
        mFileRepositoriesAdapter.setOnItemClickListener(
                new IBaseAdapter.OnItemClickListener<RepositoriesFileContent>() {
                    @Override public void onItemClick(RepositoriesFileContent item, View view,
                                                      int position) {
                        if (item.getType().equals("file")) {
                            FileDetailRepositoriesActivity.start(getActivity(), item);
                        } else if (item.getType().equals("dir")) {
                            loadPath(item.getPath());
                        }
                    }
                });

        loadPath("");
    }

    public class FileRepositoriesAdapter extends
                                         BaseRecyclerAdapter<RepositoriesFileContent, BaseRecyclerViewHolder<RepositoriesFileContent>> {

        public FileRepositoriesAdapter(Context context, List<RepositoriesFileContent> list) {
            super(context, list);
        }

        @Override public BaseRecyclerViewHolder<RepositoriesFileContent> onCreateViewHolder(
                ViewGroup parent, int viewType) {
            View view = inflate(R.layout.fragment_repositories_detail_file_list_item, parent,
                    false);
            return new FileRepositoriesViewHolder(view);
        }

        @Override public void onBindViewHolderItem(
                BaseRecyclerViewHolder<RepositoriesFileContent> holder,
                RepositoriesFileContent repositoriesFileContent, int position) {
            holder.onHandle(repositoriesFileContent);
        }
    }

    public class FileRepositoriesViewHolder
            extends BaseRecyclerViewHolder<RepositoriesFileContent> {

        @BindView(R.id.repositories_detail_file_list_item_name) TextView name;

        public FileRepositoriesViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override public void onHandle(RepositoriesFileContent item) {
            name.setText(item.getName());
        }
    }

    private void loadPath(final String pathName) {
        Observable<List<RepositoriesFileContent>> repositoriesObservable = NetworkClient.get()
                .getReposService().getRepositoriesPathContent(mRepositories.getOwner().getLogin(),
                        mRepositories.getName(), pathName).subscribeOn(Schedulers.io());
        OkHttp3Utils.get().addSubscribe(repositoriesObservable,
                new Callback.EmptyCallback<List<RepositoriesFileContent>>() {
                    @Override public void onPreExecute() {
                        mRecyclerViewHelper.setRefreshCallback(true);
                        mRecyclerViewHelper.getRecyclerView().setEnabled(false);
                    }

                    @Override public void onPostExecute() {
                        mRecyclerViewHelper.setRefreshCallback(false);
                        mRecyclerViewHelper.getRecyclerView().setEnabled(true);
                    }

                    @Override public void onSuccess(List<RepositoriesFileContent> o) {
                        mFileRepositoriesAdapter.update(o);
                        mFileRepositoriesAdapter.notifyDataSetChanged();
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

}
