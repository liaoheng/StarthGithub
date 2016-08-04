package me.liaoheng.starth.github.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.github.liaoheng.common.util.Base64;
import com.github.liaoheng.common.util.StringUtils;
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.Utils;
import me.liaoheng.starth.github.model.RepositoriesFileContent;
import me.liaoheng.starth.github.ui.repositories.FileDetailRepositoriesActivity;
import org.apache.commons.io.FilenameUtils;

/**
 * @author liaoheng
 * @version 2016-07-20 13:48
 */
public class MarkdownAndCodeHighlightView extends WebView {

    private final String TAG = MarkdownAndCodeHighlightView.class.getSimpleName();

    public MarkdownAndCodeHighlightView(Context context) {
        this(context, null);
    }

    public MarkdownAndCodeHighlightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkdownAndCodeHighlightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public MarkdownAndCodeHighlightView(Context context,
                                                                                 AttributeSet attrs,
                                                                                 int defStyleAttr,
                                                                                 int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    final class Operate {
        @JavascriptInterface public void onLoad() {
            post(new Runnable() {
                @Override public void run() {
                    loadUrl("javascript:setContent('" + mData + "')");
                }
            });
        }
    }

    @SuppressLint({ "SetJavaScriptEnabled", "AddJavascriptInterface" }) private void init() {
        if (isInEditMode()) {
            return;
        }
        setWebViewClient(new MWebViewClient());
        getSettings().setJavaScriptEnabled(true);
        getSettings().setSupportZoom(true);
        getSettings().setDisplayZoomControls(false);
        addJavascriptInterface(new Operate(), "android");
    }

    private String mData;

    private String mBaseUrl;

    public void setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    public void loadMarkdownData(String data) {
        mData = Base64.encodeToString(data.getBytes());
        loadUrl("file:///android_asset/markdown-view.html");
    }

    public void loadHighlightData(String data) {
        mData = Base64.encodeToString(data.getBytes());
        loadUrl("file:///android_asset/highlight-view.html");
    }

    class MWebViewClient extends WebViewClient {
        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i(TAG, "url:" + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (TextUtils.isEmpty(mBaseUrl) || !url.startsWith("file:///android_asset/")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            } else {
                url = StringUtils.replace(url, "file:///android_asset/", mBaseUrl);
                RepositoriesFileContent file = new RepositoriesFileContent();
                file.setUrl(url);
                file.setName(FilenameUtils.getName(url));
                FileDetailRepositoriesActivity.start(getContext(), file);
            }
            return true;
        }
    }

}
