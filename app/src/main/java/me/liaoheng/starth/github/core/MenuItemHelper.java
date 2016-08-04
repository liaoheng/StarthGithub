package me.liaoheng.starth.github.core;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.Utils;
import me.liaoheng.starth.github.R;
import org.apache.commons.io.FilenameUtils;

/**
 * @author liaoheng
 * @version 2016-07-22 14:21
 */
public class MenuItemHelper {

    private MenuItemHelper() {
    }

    public static MenuItemHelper with() {
        return new MenuItemHelper();
    }

    public void onWebCreateOptionsMenu(MenuInflater inflater, Menu menu) {
        inflater.inflate(R.menu.main_web, menu);
    }

    public boolean onWebOptionsItemSelected(Context context, MenuItem item, String url,
                                            String downloadUrl) {
        int itemId_ = item.getItemId();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (itemId_ == R.id.menu_web_share) {
            Uri uri = Uri.parse(url);
            Intent share = new Intent(Intent.ACTION_SEND, uri);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.putExtra(Intent.EXTRA_TEXT, uri.toString());
            context.startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        } else if (itemId_ == R.id.menu_web_open_with_browser) {
            Uri uri = Uri.parse(url);
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            browser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browser);
            return true;
        } else if (itemId_ == R.id.menu_web_copy_url) {
            ClipboardManager clipboard = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", url);
            clipboard.setPrimaryClip(clip);
            UIUtils.showToast(context.getApplicationContext(), context.getString(R.string.success));
            return true;
        } else if (itemId_ == R.id.menu_web_download) {
            String extension = FilenameUtils.getExtension(downloadUrl);
            if (TextUtils.isEmpty(extension)) {
                UIUtils.showToast(context.getApplicationContext(),
                        context.getString(R.string.failed_to_download));
            } else {
                String fileName = FilenameUtils.getName(downloadUrl);
                if (TextUtils.isEmpty(fileName)) {
                    fileName = "NoName";
                }
                Utils.systemDownloadPublicDir(context, fileName, downloadUrl,
                        Environment.DIRECTORY_DOWNLOADS, fileName);
            }
        }
        return false;
    }

    public boolean onWebOptionsItemSelected(Context context, MenuItem item, String url) {
        return onWebOptionsItemSelected(context, item, url, url);
    }
}
