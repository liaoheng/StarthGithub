package me.liaoheng.github.starth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.github.liaoheng.common.plus.core.WebHelper;
import com.github.liaoheng.common.plus.view.WebViewLayout;
import com.github.liaoheng.common.util.UIUtils;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.core.MenuItemHelper;
import me.liaoheng.github.starth.ui.base.BaseActivity;

/**
 * @author liaoheng
 * @version 2016-06-26 12:47
 */
public class WebViewActivity extends BaseActivity {

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private WebHelper      mWebHelper;
    private MenuItemHelper mMenuItemHelper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initToolBar();
        initSlidrStatusBar();

        getBaseActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);

        mMenuItemHelper = MenuItemHelper.with();

        mWebHelper = WebHelper.with(UIUtils.findViewById(this, R.id.lcp_web_view));
        mWebHelper.getWebView().setWebChromeClient(new WebChromeClient(this));
        WebSettings settings = mWebHelper.getWebView().getWebView().getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        String url = getIntent().getStringExtra("url");
        mWebHelper.loadUrl(url);
    }

    class WebChromeClient extends WebViewLayout.CPWebChromeClient {

        public WebChromeClient(Object context) {
            super(context);
        }

        @Override public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }

    @Override protected void onPause() {
        mWebHelper.onPause();
        super.onPause();
    }

    @Override protected void onResume() {
        mWebHelper.onResume();
        super.onResume();
    }

    @Override protected void onDestroy() {
        mWebHelper.onDestroy();
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        mWebHelper.onBackPressed(this);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mWebHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuItemHelper.onWebOptionsItemSelected(this, item,
                mWebHelper.getWebView().getWebView().getUrl()) || super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        mMenuItemHelper.onWebCreateOptionsMenu(getMenuInflater(), menu);
        return super.onCreateOptionsMenu(menu);
    }
}
