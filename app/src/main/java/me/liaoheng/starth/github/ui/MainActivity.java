package me.liaoheng.starth.github.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.flyco.systembar.SystemBarHelper;
import com.github.liaoheng.common.plus.adapter.holder.BaseViewHolder;
import com.github.liaoheng.common.plus.core.TabPagerHelper;
import com.github.liaoheng.common.plus.model.PagerTab;
import com.github.liaoheng.common.util.UIUtils;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.model.User;
import me.liaoheng.starth.github.model.UserLogin;
import me.liaoheng.starth.github.ui.base.BaseActivity;
import me.liaoheng.starth.github.util.Constants;

/**
 * 主界面
 * @author liaoheng
 * @version 2016-06-24 19:39:37
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.drawer_layout)     DrawerLayout   mDrawerLayout;
    @BindView(R.id.navigation_drawer) NavigationView mNavigationView;

    TabPagerHelper mTabPagerHelper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBar();
        SystemBarHelper.tintStatusBarForDrawer(this, mDrawerLayout,
                ContextCompat.getColor(this, R.color.colorPrimaryDark), 0);

        boolean login = UserLogin.get().isLogin();
        if (!login) {
            UIUtils.startActivity(this, OAuthActivity.class);
            finish();
            return;
        }

        getBaseActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_home);
        setupDrawerContent(mNavigationView);

        User user = UserLogin.get().getUser();

        PagerTab pagerTab = new PagerTab();
        pagerTab.setTabs(getString(R.string.repositories), RepositoriesFragment.newInstance(user));
        pagerTab.setTabs(getString(R.string.stars), StarsFragment.newInstance(user));
        pagerTab.setTabs(getString(R.string.followers_me), FollowersFragment.newInstance(user));
        pagerTab.setTabs(getString(R.string.following_me), FollowingFragment.newInstance(user));

        mTabPagerHelper = TabPagerHelper.with(this);
        mTabPagerHelper.getViewPager().setOffscreenPageLimit(3);
        mTabPagerHelper.setAdapter(getSupportFragmentManager(), getActivity(), pagerTab.getTabs(),
                new TabPagerHelper.TabPagerOperation() {
                    @Override public Fragment getItem(PagerTab tab, int position) {
                        return (Fragment) tab.getObject();
                    }
                });

        View headerView = mNavigationView.getHeaderView(0);
        new NavigationHeaderViewHolder(headerView).onHandle(user);
    }

    class NavigationHeaderViewHolder extends BaseViewHolder<User> {
        @BindView(R.id.user_avatar) ImageView mUserAvatar;
        @BindView(R.id.user_name)   TextView  mUserName;

        public NavigationHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.user_avatar) void openUserInfo() {
            mDrawerLayout.closeDrawers();
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            intent.putExtra(Constants.USER, UserLogin.get().getUser());
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            mUserAvatar, "xxx");
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            //UserInfoActivity.start(getActivity(), UserLogin.get().getUser());
        }

        @Override public void onHandle(User item) {
            Glide.with(getContext()).load(UserLogin.get().getAvatar()).into(mUserAvatar);
            mUserName.setText(UserLogin.get().getName());
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.menu_main_drawer_about) {
                            AboutActivity.start(getActivity());
                            return true;
                        }
                        menuItem.setChecked(true);
                        return true;
                    }
                });
    }

    private void home() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == android.R.id.home) {
            home();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        UIUtils.cancelToast();
        super.onDestroy();
    }
}
