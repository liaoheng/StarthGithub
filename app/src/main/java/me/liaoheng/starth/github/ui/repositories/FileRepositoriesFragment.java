package me.liaoheng.starth.github.ui.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.liaoheng.common.adapter.base.BaseRecyclerAdapter;
import com.github.liaoheng.common.adapter.base.IBaseAdapter;
import com.github.liaoheng.common.adapter.holder.BaseRecyclerViewHolder;
import com.github.liaoheng.common.adapter.core.RecyclerViewHelper;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.StringUtils;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.Utils;

import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.Repositories;
import me.liaoheng.starth.github.model.RepositoriesFileContent;
import me.liaoheng.starth.github.ui.base.LazyFragment;
import me.liaoheng.starth.github.util.Constants;
import org.laukvik.pretty.ByteUnit;
import org.laukvik.pretty.PrettyFormat;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * @author liaoheng
 * @version 2016-07-19 12:49
 */
public class FileRepositoriesFragment extends LazyFragment
        implements RepositoriesDetailActivity.onBackPressedListener {

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
    private Subscription mGetFileSubscription;

    @BindView(R.id.repositories_detail_file_list_breadcrumb) TextView breadcrumb;

    @Override protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_repositories_detail_file_list);
        ButterKnife.bind(this, getContentView());

        mRepositories = (Repositories) getArguments().getSerializable(Constants.REPOSITORIES);
        if (mRepositories == null) {
            return;
        }

        mRecyclerViewHelper = new RecyclerViewHelper.Builder(getActivity(), getContentView())
                .setLayoutManager().build();
        mFileRepositoriesAdapter = new FileRepositoriesAdapter(getActivity(), null);
        mRecyclerViewHelper.setAdapter(mFileRepositoriesAdapter);
        mFileRepositoriesAdapter.setOnItemClickListener(
                new IBaseAdapter.OnItemClickListener<RepositoriesFileContent>() {
                    @Override public void onItemClick(RepositoriesFileContent item, View view,
                                                      int position) {
                        if (item.getType().equals(RepositoriesFileContent.Type.file)) {
                            FileDetailRepositoriesActivity.start(getActivity(), item);
                        } else if (item.getType().equals(RepositoriesFileContent.Type.dir)) {
                            loadPath(item.getPath());
                        }
                    }
                });

        loadPath("");
    }

    @Override public void onAttach(Context context) {
        if (context instanceof RepositoriesDetailActivity) {
            ((RepositoriesDetailActivity) context).setBackPressedListener(this);
        }
        super.onAttach(context);
    }

    @Override public void onDetach() {
        if (getContext() instanceof RepositoriesDetailActivity) {
            ((RepositoriesDetailActivity) getContext()).setBackPressedListener(null);
        }
        super.onDetach();
    }

    @Override public boolean backPressed() {
        if (breadcrumb == null) {
            return true;
        }
        String path = breadcrumb.getText().toString();
        if (TextUtils.isEmpty(path) || StringUtils.equals(path, "/")) {
            return true;
        }
        int i = path.lastIndexOf("/");
        String substring = StringUtils.substring(path, 1, i);
        if (TextUtils.isEmpty(substring)) {
            loadPath("");
        } else {
            loadPath(substring);
        }
        return false;
    }

    public class FileRepositoriesAdapter extends
                                         BaseRecyclerAdapter<RepositoriesFileContent, BaseRecyclerViewHolder<RepositoriesFileContent>> {

        public FileRepositoriesAdapter(Context context, List<RepositoriesFileContent> list) {
            super(context, list);
        }

        @Override public int getItemViewType(int position) {
            RepositoriesFileContent item = getList().get(position);
            if (item.getType().equals(RepositoriesFileContent.Type.file)) {
                return 1;
            } else if (item.getType().equals(RepositoriesFileContent.Type.dir)) {
                return 2;
            }
            return super.getItemViewType(position);
        }

        @Override public BaseRecyclerViewHolder<RepositoriesFileContent> onCreateViewHolder(
                ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View view = inflate(R.layout.fragment_repositories_detail_file_list_file_item,
                        parent, false);
                return new FileRepositoriesViewHolder(view);
            } else if (viewType == 2) {
                View view = inflate(R.layout.fragment_repositories_detail_file_list_folder_item,
                        parent, false);
                return new FolderRepositoriesViewHolder(view);
            } else {
                throw new IllegalArgumentException("type is error");
            }
        }

        @Override public void onBindViewHolderItem(
                BaseRecyclerViewHolder<RepositoriesFileContent> holder,
                RepositoriesFileContent repositoriesFileContent, int position) {
            holder.onHandle(repositoriesFileContent,position);
        }
    }

    public class FolderRepositoriesViewHolder
            extends BaseRecyclerViewHolder<RepositoriesFileContent> {
        @BindView(R.id.repositories_detail_file_list_item_name) TextView name;

        public FolderRepositoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override public void onHandle(RepositoriesFileContent item,int position) {
            name.setText(item.getName());
        }
    }

    public class FileRepositoriesViewHolder
            extends BaseRecyclerViewHolder<RepositoriesFileContent> {

        @BindView(R.id.repositories_detail_file_list_item_name) TextView name;
        @BindView(R.id.repositories_detail_file_list_item_size) TextView size;

        public FileRepositoriesViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override public void onHandle(RepositoriesFileContent item,int position) {
            name.setText(item.getName());
            String format = PrettyFormat.with().setUnit(ByteUnit.Kb).setFractionDigits(2)
                    .format(item.getSize());
            size.setText(format);
        }
    }

    @SuppressLint("SetTextI18n") private void loadPath(final String pathName) {
        if (TextUtils.isEmpty(pathName)) {
            breadcrumb.setText("/");
        } else {
            breadcrumb.setText("/" + pathName);
        }

        if (mGetFileSubscription != null) {
            if (!mGetFileSubscription.isUnsubscribed()) {
                mGetFileSubscription.unsubscribe();
            }
        }

        Observable<List<RepositoriesFileContent>> repositoriesObservable = NetworkClient.get()
                .getReposService().getRepositoriesPathContent(mRepositories.getOwner().getLogin(),
                        mRepositories.getName(), pathName).subscribeOn(Schedulers.io());
        mGetFileSubscription = Utils.addSubscribe(repositoriesObservable,
                new Callback.EmptyCallback<List<RepositoriesFileContent>>() {
                    @Override public void onPreExecute() {
                        mRecyclerViewHelper.setSwipeRefreshing(true);
                        mRecyclerViewHelper.getRecyclerView().setEnabled(false);
                    }

                    @Override public void onPostExecute() {
                        mRecyclerViewHelper.setSwipeRefreshing(false);
                        mRecyclerViewHelper.getRecyclerView().setEnabled(true);
                    }

                    @Override public void onSuccess(List<RepositoriesFileContent> o) {
                        mFileRepositoriesAdapter.setList(o);
                        mFileRepositoriesAdapter.notifyDataSetChanged();
                    }

                    @Override public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

}
