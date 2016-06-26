package me.liaoheng.github.starth.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.github.liaoheng.common.plus.core.ListDuplicateHelper;
import java.util.List;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.adapter.viewholder.FollowersViewHolder;
import me.liaoheng.github.starth.model.Followers;

/**
 * @author liaoheng
 * @version 2016-06-25 19:44:38
 */
public class FollowersAdapter extends BaseAdapter<Followers, FollowersViewHolder> {

    ListDuplicateHelper<Followers> mListDuplicateHelper;

    public FollowersAdapter(Context context, List<Followers> list) {
        super(context, list);
        mListDuplicateHelper = new ListDuplicateHelper<>(getList());
    }

    @Override public void add(Followers o) {
        mListDuplicateHelper.addDuplicate(o, o.getId());
    }

    @Override public void add(int index, Followers o) {
        mListDuplicateHelper.addDuplicate(index, o, o.getId());
    }

    @Override public void addAll(List<Followers> list) {
        mListDuplicateHelper.addAllDuplicate(list, new ListDuplicateHelper.Key<Followers>() {
            @Override public int getKey(Followers item) {
                return item.getId();
            }
        });
    }

    @Override public void addAll(int index, List<Followers> list) {
        mListDuplicateHelper.addAllDuplicate(index, list, new ListDuplicateHelper.Key<Followers>() {
            @Override public int getKey(Followers item) {
                return item.getId();
            }
        });
    }

    @Override public void update(List<Followers> list) {
        mListDuplicateHelper.updateDuplicate(list, new ListDuplicateHelper.Key<Followers>() {
            @Override public int getKey(Followers item) {
                return item.getId();
            }
        });
    }

    @Override public FollowersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate(R.layout.fragment_followers_item, parent, false);
        return new FollowersViewHolder(view);
    }

    @Override public void onBindViewHolderItem(FollowersViewHolder holder, Followers followers,
                                               int position) {
        holder.onHandle(followers);
    }
}
