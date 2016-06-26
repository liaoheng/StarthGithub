package me.liaoheng.github.starth.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.liaoheng.common.plus.adapter.holder.BaseRecyclerViewHolder;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.model.Followers;

/**
 * @author liaoheng
 * @version 2016-06-25 19:45:12
 */
public class FollowersViewHolder extends BaseRecyclerViewHolder<Followers> {

    @BindView(R.id.followers_list_item_avatar) ImageView avatar;
    @BindView(R.id.followers_list_item_name)   TextView  name;

    public FollowersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onHandle(Followers item) {
        Glide.with(getContext()).load(item.getAvatar_url()).into(avatar);
        name.setText(item.getLogin());
    }
}
