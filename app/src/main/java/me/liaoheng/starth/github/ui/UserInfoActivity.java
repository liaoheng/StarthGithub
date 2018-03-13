package me.liaoheng.starth.github.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.KMNumbers;
import com.flyco.systembar.SystemBarHelper;
import com.github.liaoheng.common.ui.core.TabPagerHelper;
import com.github.liaoheng.common.ui.model.PagerTab;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.SystemRuntimeException;
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.Utils;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.liaoheng.starth.github.R;
import me.liaoheng.starth.github.data.net.NetworkClient;
import me.liaoheng.starth.github.model.User;
import me.liaoheng.starth.github.ui.base.BaseActivity;
import me.liaoheng.starth.github.util.Constants;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * User info
 *
 * @author liaoheng
 * @version 2016-06-25 20:13
 */
public class UserInfoActivity extends BaseActivity {
    User mUser;

    @BindView(R.id.user_info_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.lcp_app_bar_layout) AppBarLayout mAppBarLayout;

    @BindView(R.id.user_info_cover) ImageView userCover;
    @BindView(R.id.user_info_avatar) ImageView avatar;
    @BindView(R.id.user_info_name) TextView name;
    @BindView(R.id.user_info_login_name) TextView loginName;
    @BindView(R.id.user_info_desc) TextView desc;
    @BindView(R.id.user_info_followers) TextView followers;
    @BindView(R.id.user_info_following) TextView following;

    TabPagerHelper mTabPagerHelper;
    ProgressDialog mFollowProgressDialog;
    Subscription mFollowSubscription;

    public static void start(Context context, User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.USER, user);
        UIUtils.startActivity(context, UserInfoActivity.class, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        ButterKnife.bind(this);
        initView();
        mUser = (User) getIntent().getSerializableExtra(Constants.USER);
        if (mUser == null) {
            L.getToast().e(TAG, getApplicationContext(), "user is null");
            return;
        }

        initBaseInfo();

        mFollowProgressDialog = UIUtils.createProgressDialog(this, "");
        mFollowProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utils.unsubscribe(mFollowSubscription);
            }
        });

        load();
    }

    protected void initData() {
        initBaseInfo();
        String bio = mUser.getBio();
        bio = TextUtils.isEmpty(bio) ? mUser.getLocation() : bio;
        if (!TextUtils.isEmpty(bio)) {
            desc.setText(bio);
        }

        String ers = KMNumbers.formatNumbers((long) mUser.getFollowers());
        String ing = KMNumbers.formatNumbers((long) mUser.getFollowing());
        followers.setText(ers);
        following.setText(ing);

        PagerTab pagerTab = new PagerTab();
        pagerTab.setTabs(getString(R.string.repositories), RepositoriesFragment.newInstance(mUser));
        pagerTab.setTabs(getString(R.string.stars), StarsFragment.newInstance(mUser));
        pagerTab.setTabs(getString(R.string.followers_another),
                FollowersFragment.newInstance(mUser));
        pagerTab.setTabs(getString(R.string.following_another),
                FollowingFragment.newInstance(mUser));

        mTabPagerHelper.getViewPager().setOffscreenPageLimit(3);
        mTabPagerHelper.setAdapter(getSupportFragmentManager(), getActivity(), pagerTab.getTabs(),
                new TabPagerHelper.TabPagerOperation() {
                    @Override
                    public Fragment getItem(PagerTab tab, int position) {
                        return (Fragment) tab.getObject();
                    }
                });
    }

    private void initBaseInfo() {
        Glide.with(this).load(mUser.getAvatar_url()).dontAnimate().into(avatar);
        name.setText(mUser.getName());
        loginName.setText(mUser.getLogin());
    }

    public void initView() {
        initToolBar();
        SystemBarHelper.immersiveStatusBar(this, 0);
        mTabPagerHelper = TabPagerHelper.with(this);
        SystemBarHelper.setHeightAndPadding(this, getToolbar());
        Slidr.attach(this, new SlidrConfig.Builder().edge(true).build());
        //↓use user2.xml
        SystemBarHelper.setPadding(this, mTabPagerHelper.getTabLayout());
        getBaseActionBar().setDisplayShowTitleEnabled(false);
        getBaseActionBar().setDisplayHomeAsUpEnabled(false);
        //setTitle("");
        //mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        //    boolean isShow = false;
        //    int scrollRange = -1;
        //
        //    @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //        if (scrollRange == -1) {
        //            scrollRange = appBarLayout.getTotalScrollRange();
        //        }
        //        if (scrollRange + verticalOffset <= 0) {
        //            setTitle(mUser.getName());
        //            isShow = true;
        //        } else if (isShow) {
        //            setTitle("");
        //            isShow = false;
        //        }
        //    }
        //});

        //↑use user2.xml
    }

    private void load() {
        Observable<User> userObservable = NetworkClient.get().getUserService()
                .getUser(mUser.getLogin()).subscribeOn(Schedulers.io()).compose(this.<User>bindToLifecycle())
                .map(new Func1<User, User>() {
                    @Override
                    public User call(User user) {
                        try {
                            Response<ResponseBody> execute = NetworkClient.get().getUserService()
                                    .checkIsFollow(user.getLogin()).execute();
                            user.setFollow(
                                    NetworkClient.get().checkGithubResponseBodyStatus(execute));
                        } catch (IOException | SystemRuntimeException e) {
                            throw new SystemRuntimeException(e);
                        }
                        return user;
                    }
                });
        mFollowSubscription = Utils
                .addSubscribe(userObservable, new Callback.EmptyCallback<User>() {

                    @Override
                    public void onSuccess(User user) {
                        if (user == null) {
                            onError(new SystemException("user is null"));
                            return;
                        }
                        mUser = user;
                        invalidateOptionsMenu();
                        initData();
                    }

                    @Override
                    public void onError(SystemException e) {
                        L.getToast().e(TAG, getApplicationContext(), e);
                    }
                });
    }

    private void follow() {

        mFollowProgressDialog.setMessage(mUser.isFollow()
                ? getString(R.string.menu_user_info_unfollow) + "..."
                : getString(R.string.menu_user_info_follow) + "...");

        Observable<Boolean> followObservable = Observable.just(mUser).subscribeOn(Schedulers.io())
                .map(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        try {
                            Response<ResponseBody> execute;
                            if (user.isFollow()) {
                                execute = NetworkClient.get().getUserService()
                                        .unFollow(user.getLogin()).execute();
                            } else {
                                execute = NetworkClient.get().getUserService()
                                        .follow(user.getLogin()).execute();
                            }
                            return NetworkClient.get().checkGithubResponseBodyStatus(execute);
                        } catch (IOException e) {
                            throw new SystemRuntimeException(e);
                        }
                    }
                });

        Utils.addSubscribe(followObservable, new Callback.EmptyCallback<Boolean>() {
            @Override
            public void onPreExecute() {
                UIUtils.showDialog(mFollowProgressDialog);
            }

            @Override
            public void onPostExecute() {
                UIUtils.dismissDialog(mFollowProgressDialog);
            }

            @Override
            public void onSuccess(Boolean isOk) {
                UIUtils.showToast(getApplicationContext(),
                        isOk ? getString(R.string.success) : getString(R.string.fail));
                mUser.setFollow(isOk != mUser.isFollow());
                invalidateOptionsMenu();
            }

            @Override
            public void onError(SystemException e) {
                L.getToast().e(TAG, getApplicationContext(), e);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.menu_user_info_following) {
            follow();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_user_info_following);
        if (item != null) {
            if (mUser.isFollow()) {
                item.setTitle(getString(R.string.menu_user_info_unfollow));
            } else {
                item.setTitle(getString(R.string.menu_user_info_follow));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
