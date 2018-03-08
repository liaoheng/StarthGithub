package me.liaoheng.starth.github.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.github.liaoheng.common.core.ListDuplicateHelper;
import java.util.List;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.adapter.viewholder.FollowingViewHolder;
import me.liaoheng.starth.github.model.Following;

/**
 * @author liaoheng
 * @version 2016-06-25 19:44:38
 */
public class FollowingAdapter extends BaseAdapter<Following, FollowingViewHolder> {

    ListDuplicateHelper<Following> mListDuplicateHelper;

    public FollowingAdapter(Context context) {
        super(context);
        mListDuplicateHelper = new ListDuplicateHelper<>(getList());
    }

    @Override public void add(Following o) {
        mListDuplicateHelper.addDuplicate(o, o.getId());
    }

    @Override public void add(int index, Following o) {
        mListDuplicateHelper.addDuplicate(index, o, o.getId());
    }

    @Override public void addAll(List<Following> list) {
        mListDuplicateHelper.addAllDuplicate(list, new ListDuplicateHelper.Key<Following>() {
            @Override public int getKey(Following item) {
                return item.getId();
            }
        });
    }

    @Override public void addAll(int index, List<Following> list) {
        mListDuplicateHelper.addAllDuplicate(index, list, new ListDuplicateHelper.Key<Following>() {
            @Override public int getKey(Following item) {
                return item.getId();
            }
        });
    }

     public void update(List<Following> list) {
        mListDuplicateHelper.updateDuplicate(list, new ListDuplicateHelper.Key<Following>() {
            @Override public int getKey(Following item) {
                return item.getId();
            }
        });
    }

    @Override public FollowingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate(R.layout.fragment_followers_item, parent, false);
        return new FollowingViewHolder(view);
    }

    @Override public void onBindViewHolderItem(FollowingViewHolder holder, Following following,
                                               int position) {
        holder.onHandle(following,position);
    }
}
