package me.liaoheng.github.starth.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.github.liaoheng.common.plus.core.ListDuplicateHelper;
import java.util.List;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.adapter.viewholder.StarsViewHolder;
import me.liaoheng.github.starth.model.Star;

/**
 * @author liaoheng
 * @version 2016-06-25 19:18
 */
public class StarsAdapter extends BaseAdapter<Star, StarsViewHolder> {

    ListDuplicateHelper<Star> mListDuplicateHelper;

    public StarsAdapter(Context context, List<Star> list) {
        super(context, list);
        mListDuplicateHelper = new ListDuplicateHelper<>(getList());
    }

    @Override public void add(Star o) {
        mListDuplicateHelper.addDuplicate(o, o.getId());
    }

    @Override public void add(int index, Star o) {
        mListDuplicateHelper.addDuplicate(index, o, o.getId());
    }

    @Override public void addAll(List<Star> list) {
        mListDuplicateHelper.addAllDuplicate(list, new ListDuplicateHelper.Key<Star>() {
            @Override public int getKey(Star item) {
                return item.getId();
            }
        });
    }

    @Override public void addAll(int index, List<Star> list) {
        mListDuplicateHelper.addAllDuplicate(index, list, new ListDuplicateHelper.Key<Star>() {
            @Override public int getKey(Star item) {
                return item.getId();
            }
        });
    }

    @Override public void update(List<Star> list) {
        mListDuplicateHelper.updateDuplicate(list, new ListDuplicateHelper.Key<Star>() {
            @Override public int getKey(Star item) {
                return item.getId();
            }
        });
    }

    @Override public StarsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate(R.layout.fragment_stars_item, parent, false);
        return new StarsViewHolder(view);
    }

    @Override public void onBindViewHolderItem(StarsViewHolder holder, Star star, int position) {
        holder.onHandle(star);
    }
}
