package me.liaoheng.starth.github.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.github.liaoheng.common.core.ListDuplicateHelper;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.viewholder.RepositoriesViewHolder;
import me.liaoheng.starth.github.model.Repositories;

/**
 * @author liaoheng
 * @version 2016-06-25 19:21
 */
public class RepositoriesAdapter extends BaseAdapter<Repositories, RepositoriesViewHolder> {

    ListDuplicateHelper<Repositories> mListDuplicateHelper;

    public RepositoriesAdapter(Context context) {
        super(context);
        mListDuplicateHelper = new ListDuplicateHelper<>(getList());
    }

    @Override public void add(Repositories o) {
        mListDuplicateHelper.addDuplicate(o, o.getId());
    }

    @Override public void add(int index, Repositories o) {
        mListDuplicateHelper.addDuplicate(index, o, o.getId());
    }

    @Override public void addAll(List<Repositories> list) {
        mListDuplicateHelper.addAllDuplicate(list, new ListDuplicateHelper.Key<Repositories>() {
            @Override public int getKey(Repositories item) {
                return item.getId();
            }
        });
    }

    @Override public void addAll(int index, List<Repositories> list) {
        mListDuplicateHelper
                .addAllDuplicate(index, list, new ListDuplicateHelper.Key<Repositories>() {
                    @Override public int getKey(Repositories item) {
                        return item.getId();
                    }
                });
    }

     public void update(List<Repositories> list) {
        mListDuplicateHelper.updateDuplicate(list, new ListDuplicateHelper.Key<Repositories>() {
            @Override public int getKey(Repositories item) {
                return item.getId();
            }
        });
    }

    @Override public RepositoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate(R.layout.fragment_stars_item, parent, false);
        return new RepositoriesViewHolder(view);
    }

    @Override public void onBindViewHolderItem(RepositoriesViewHolder holder,
                                               Repositories repositories, int position) {
        holder.onHandle(repositories,position);
    }
}

