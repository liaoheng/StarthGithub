package me.liaoheng.github.starth.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.github.liaoheng.common.plus.core.WebHelper;
import com.github.liaoheng.common.plus.view.WebViewLayout;
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.Utils;
import me.liaoheng.github.starth.R;
import me.liaoheng.github.starth.ui.base.BaseActivity;
import org.apache.commons.io.FilenameUtils;

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

    private WebHelper mWebHelper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initToolBar();
        initSlidrStatusBar();

        getBaseActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);

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
        int itemId_ = item.getItemId();
        String url = mWebHelper.getWebView().getWebView().getUrl();
        if (TextUtils.isEmpty(url)) {
            return super.onOptionsItemSelected(item);
        }
        if (itemId_ == R.id.menu_web_share) {
            Uri uri = Uri.parse(url);
            Intent share = new Intent(Intent.ACTION_SEND, uri);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.putExtra(Intent.EXTRA_TEXT, uri.toString());
            startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        } else if (itemId_ == R.id.menu_web_open_with_browser) {
            Uri uri = Uri.parse(url);
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            browser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browser);
            return true;
        } else if (itemId_ == R.id.menu_web_copy_url) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", url);
            clipboard.setPrimaryClip(clip);
            return true;
        } else if (itemId_ == R.id.menu_web_download) {
            String extension = FilenameUtils.getExtension(url);
            if (TextUtils.isEmpty(extension)) {
                UIUtils.showToast(getApplicationContext(), getString(R.string.failed_to_download));
            } else {
                String fileName = FilenameUtils.getName(url);
                Utils.systemDownloadPublicDir(this, fileName, url, Environment.DIRECTORY_DOWNLOADS,
                        fileName);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_web, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
