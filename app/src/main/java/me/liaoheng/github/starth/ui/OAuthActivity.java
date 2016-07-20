package me.liaoheng.github.starth.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.liaoheng.common.plus.core.ProgressHelper;
import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.SystemRuntimeException;
import com.github.liaoheng.common.util.UIUtils;
import java.io.IOException;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.data.net.NetworkClient;
import me.liaoheng.github.starth.data.net.OAuthService;
import me.liaoheng.github.starth.model.AccessToken;
import me.liaoheng.github.starth.model.RequestToken;
import me.liaoheng.github.starth.model.User;
import me.liaoheng.github.starth.model.UserLogin;
import me.liaoheng.github.starth.ui.base.BaseActivity;
import me.liaoheng.github.starth.util.Constants;
import me.liaoheng.github.starth.util.RetrofitUtils;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Github OAuth
 * @author liaoheng
 * @version 2016-06-24 22:30
 */
public class OAuthActivity extends BaseActivity {

    @BindView(R.id.oauth_web_view) WebView mOAuthWebView;
    ProgressHelper mProgressHelper;

    private ProgressDialog mProgressDialog;

    private Subscription subscription;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        ButterKnife.bind(this);
        initStatusBar();

        mProgressHelper = ProgressHelper.with(this);

        WebSettings settings = mOAuthWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSaveFormData(false);

        mProgressDialog = UIUtils.createProgressDialog(getActivity(), "Get Token...");
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog) {
                subscription.unsubscribe();
            }
        });

        mOAuthWebView.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressHelper.visible();
            }

            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressHelper.gone();
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (!url.startsWith(Constants.REDIRECT_URL)) {
                    return false;
                }

                String code = Uri.parse(url).getQueryParameter("code");//得到授权Code

                Observable<User> observable = RetrofitUtils.get().getRetrofit()
                        .create(OAuthService.class).getToken(
                                new RequestToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET,
                                        code)).subscribeOn(Schedulers.io())
                        .map(new Func1<AccessToken, AccessToken>() {
                            @Override public AccessToken call(AccessToken accessToken) {
                                if (!TextUtils.isEmpty(accessToken.getError())) {
                                    throw new SystemRuntimeException(accessToken.getError());
                                }
                                L.d(TAG, "access_token  : %s", accessToken);
                                UserLogin.get().setAccessToken(accessToken.getAccess_token());
                                OkHttp3Utils.get()
                                        .updateHeader(UserLogin.get().getAccessTokenHeaderKey(),
                                                UserLogin.get().getAccessTokenHeaderContent());
                                return accessToken;
                            }
                        }).map(new Func1<AccessToken, User>() {
                            @Override public User call(AccessToken accessToken) {
                                try {
                                    Response<User> execute = NetworkClient.get().getUserService()
                                            .getCurUserCall().execute();
                                    boolean successful = execute.isSuccessful();
                                    if (!successful) {
                                        throw new SystemRuntimeException(
                                                execute.errorBody().string());
                                    }
                                    User user = execute.body();
                                    L.d(TAG, "cur user  : %s", user);
                                    UserLogin.get().clear();
                                    UserLogin.get().login(user.getStringId(), user.getName(),
                                            user.getLogin(), user.getAvatar_url(),
                                            accessToken.getAccess_token());
                                    UserLogin.get().saveLoginInfo();
                                    return user;
                                } catch (IOException e) {
                                    throw new SystemRuntimeException(e);
                                }
                            }
                        });

                subscription = OkHttp3Utils.get()
                        .addSubscribe(observable, new Callback.EmptyCallback<User>() {

                            @Override public void onPreExecute() {
                                UIUtils.showDialog(mProgressDialog);
                            }

                            @Override public void onPostExecute() {
                                UIUtils.dismissDialog(mProgressDialog);
                            }

                            @Override public void onSuccess(User user) {
                                UIUtils.showToast(getApplicationContext(), "OAuth success!");
                                UIUtils.startActivity(getActivity(), MainActivity.class);
                                finish();
                            }

                            @Override public void onError(SystemException e) {
                                L.getToast().e(TAG, getActivity(), e);
                            }
                        });
                return true;
            }
        });

        mOAuthWebView.loadUrl(
                Constants.OAUTH_URL + "?client_id=" + Constants.CLIENT_ID + "&scope=user%20repo");
    }

    @Override protected void onResume() {
        mOAuthWebView.onResume();
        super.onResume();
    }

    @Override protected void onPause() {
        mOAuthWebView.onPause();
        super.onPause();
    }

    @Override protected void onDestroy() {
        mOAuthWebView.destroy();
        super.onDestroy();
    }
}
