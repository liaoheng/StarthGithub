package me.liaoheng.starth.github.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.liaoheng.common.adapter.holder.BaseRecyclerViewHolder;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.model.Following;

/**
 * @author liaoheng
 * @version 2016-06-25 20:00:51
 */
public class FollowingViewHolder extends BaseRecyclerViewHolder<Following> {

    @BindView(R.id.followers_list_item_avatar) ImageView avatar;
    @BindView(R.id.followers_list_item_name)   TextView  name;

    public FollowingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onHandle(Following item, int position) {
        Glide.with(getContext()).load(item.getAvatar_url()).into(avatar);
        name.setText(item.getLogin());
    }
}
