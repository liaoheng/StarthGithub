package me.liaoheng.starth.github.adapter.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.liaoheng.common.adapter.holder.BaseRecyclerViewHolder;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.model.Repositories;
import me.liaoheng.starth.github.model.User;
import me.liaoheng.starth.github.model.UserLogin;
import me.liaoheng.starth.github.ui.UserInfoActivity;
import me.liaoheng.starth.github.util.Constants;

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

    @Override public void onHandle(final Repositories item, int position) {
        Glide.with(getContext()).load(item.getOwner().getAvatar_url()).into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                intent.putExtra(Constants.USER, new User(item.getOwner()));
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(),
                                v, "xxx");
                ActivityCompat.startActivity((Activity) getContext(), intent, options.toBundle());

                //UserInfoActivity
                //        .start(getContext(), new User().setLogin(item.getOwner().getLogin()));
            }
        });
        title.setText(item.getName());
        desc.setText(item.getDescription());
        language.setText(item.getLanguage());
        watchers.setText(String.valueOf(item.getWatchers()));
        forks.setText(String.valueOf(item.getForks()));
    }
}
