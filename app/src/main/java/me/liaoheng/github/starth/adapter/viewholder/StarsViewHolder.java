package me.liaoheng.github.starth.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.liaoheng.common.plus.adapter.holder.BaseRecyclerViewHolder;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.model.Repositories;
import me.liaoheng.github.starth.model.User;
import me.liaoheng.github.starth.ui.UserInfoActivity;

/**
 * @author liaoheng
 * @version 2016-06-25 19:18
 */
public class StarsViewHolder extends BaseRecyclerViewHolder<Repositories> {
    @BindView(R.id.stars_list_item_image)    ImageView image;
    @BindView(R.id.stars_list_item_title)    TextView  title;
    @BindView(R.id.stars_list_item_desc)     TextView  desc;
    @BindView(R.id.stars_list_item_language) TextView  language;
    @BindView(R.id.stars_list_item_watchers) TextView  watchers;
    @BindView(R.id.stars_list_item_forks)    TextView  forks;

    public StarsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onHandle(final Repositories item) {
        Glide.with(getContext()).load(item.getOwner().getAvatar_url()).into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                UserInfoActivity
                        .start(getContext(), new User().setLogin(item.getOwner().getLogin()));
            }
        });
        title.setText(item.getName());
        desc.setText(item.getDescription());
        language.setText(item.getLanguage());
        watchers.setText(String.valueOf(item.getWatchers()));
        forks.setText(String.valueOf(item.getForks()));
    }
}
