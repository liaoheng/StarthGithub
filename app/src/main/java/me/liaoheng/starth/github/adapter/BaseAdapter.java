package me.liaoheng.starth.github.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.github.liaoheng.common.plus.adapter.BaseRecyclerAdapter;
import java.util.List;

/**
 * @author liaoheng
 * @version 2016-06-25 18:49
 */
public abstract class BaseAdapter<K, V extends RecyclerView.ViewHolder>
        extends BaseRecyclerAdapter<K, V> {

    public BaseAdapter(Context context, List<K> list) {
        super(context, list);
    }

    public void itemInserted(int position) {
        if (getItemCount() == getOldSize()) {
            return;
        }
        super.notifyItemInserted(position);
    }

}
