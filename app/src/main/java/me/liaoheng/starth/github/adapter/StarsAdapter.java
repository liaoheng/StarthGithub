package me.liaoheng.starth.github.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.github.liaoheng.common.plus.core.ListDuplicateHelper;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.viewholder.StarsViewHolder;
import me.liaoheng.starth.github.model.Repositories;

/**
 * @author liaoheng
 * @version 2016-06-25 19:18
 */
public class StarsAdapter extends BaseAdapter<Repositories, StarsViewHolder> {

    ListDuplicateHelper<Repositories> mListDuplicateHelper;

    public StarsAdapter(Context context, List<Repositories> list) {
        super(context, list);
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
        mListDuplicateHelper.addAllDuplicate(index, list, new ListDuplicateHelper.Key<Repositories>() {
            @Override public int getKey(Repositories item) {
                return item.getId();
            }
        });
    }

    @Override public void update(List<Repositories> list) {
        mListDuplicateHelper.updateDuplicate(list, new ListDuplicateHelper.Key<Repositories>() {
            @Override public int getKey(Repositories item) {
                return item.getId();
            }
        });
    }

    @Override public StarsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate(R.layout.fragment_stars_item, parent, false);
        return new StarsViewHolder(view);
    }

    @Override public void onBindViewHolderItem(StarsViewHolder holder, Repositories star, int position) {
        holder.onHandle(star);
    }
}
