package me.liaoheng.starth.github.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.liaoheng.common.adapter.holder.BaseRecyclerViewHolder;
import com.github.liaoheng.common.util.UIUtils;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.model.Repositories;

/**
 * @author liaoheng
 * @version 2016-06-25 19:21
 */
public class RepositoriesViewHolder extends BaseRecyclerViewHolder<Repositories> {
    @BindView(R.id.stars_list_item_image)    ImageView image;
    @BindView(R.id.stars_list_item_title)    TextView  title;
    @BindView(R.id.stars_list_item_desc)     TextView  desc;
    @BindView(R.id.stars_list_item_language) TextView  language;
    @BindView(R.id.stars_list_item_watchers) TextView  watchers;
    @BindView(R.id.stars_list_item_forks)    TextView  forks;

    public RepositoriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onHandle(Repositories item,int position) {
        UIUtils.viewGone(image);
        title.setText(item.getName());
        desc.setText(item.getDescription());
        language.setText(item.getLanguage());
        watchers.setText(String.valueOf(item.getWatchers()));
        forks.setText(String.valueOf(item.getForks()));
    }
}
