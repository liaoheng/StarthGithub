package me.liaoheng.github.starth.ui.repositories;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import butterknife.ButterKnife;
import com.github.liaoheng.common.plus.core.TabPagerHelper;
import com.github.liaoheng.common.plus.model.PagerTab;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.UIUtils;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.model.Repositories;
import me.liaoheng.github.starth.ui.base.BaseActivity;
import me.liaoheng.github.starth.util.Constants;

/**
 * @author liaoheng
 * @version 2016-07-19 11:50
 */
public class RepositoriesDetailActivity extends BaseActivity {

    public static void start(Context context, Repositories repositories) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.REPOSITORIES, repositories);
        UIUtils.startActivity(context, RepositoriesDetailActivity.class, bundle);
    }

    TabPagerHelper mTabPagerHelper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories_detail);
        ButterKnife.bind(this);
        initToolBar();
        initSlidrStatusBar();

        Repositories repositories = (Repositories) getIntent()
                .getSerializableExtra(Constants.REPOSITORIES);
        if (repositories == null) {
            L.getToast().e(TAG, getApplicationContext(), "Repositories is null");
            return;
        }

        setTitle(repositories.getFullName());

        PagerTab pagerTab = new PagerTab();
        pagerTab.setTabs("ReadMe", ReadMeRepositoriesFragment.newInstance(repositories));
        pagerTab.setTabs("File", FileRepositoriesFragment.newInstance(repositories));
        pagerTab.setTabs(getString(R.string.followers_me),
                ReadMeRepositoriesFragment.newInstance(repositories));
        pagerTab.setTabs(getString(R.string.following_me),
                ReadMeRepositoriesFragment.newInstance(repositories));

        mTabPagerHelper = TabPagerHelper.with(this);
        mTabPagerHelper.getViewPager().setOffscreenPageLimit(3);
        mTabPagerHelper.setAdapter(getSupportFragmentManager(), getActivity(), pagerTab.getTabs(),
                new TabPagerHelper.TabPagerOperation() {
                    @Override public Fragment getItem(PagerTab tab, int position) {
                        return (Fragment) tab.getObject();
                    }
                });
    }
}
