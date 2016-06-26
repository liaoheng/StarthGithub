package me.liaoheng.github.starth.data.net;

import android.text.TextUtils;
import com.github.liaoheng.common.util.StringUtils;
import me.liaoheng.github.starth.data.Page;
import me.liaoheng.github.starth.data.PaginationLink;
import me.liaoheng.github.starth.data.RelType;
import me.liaoheng.github.starth.util.RetrofitUtils;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author liaoheng
 * @version 2016-06-25 13:48
 */
public class NetworkClient {
    private NetworkClient() {
    }

    private static NetworkClient client;

    public static synchronized NetworkClient get() {
        if (client == null) {
            synchronized (NetworkClient.class) {
                if (client == null) {
                    client = new NetworkClient();
                }
            }
        }
        return client;
    }

    private UserService mUserService;

    public UserService getUserService() {
        if (mUserService == null) {
            mUserService = RetrofitUtils.get().getRetrofit().create(UserService.class);
        }
        return mUserService;
    }

    public boolean checkGithubResponseBodyStatus(Response<ResponseBody> execute) {
        if (execute.code() == 204) {
            return true;
        } else {
            return false;
        }
    }

    public void setPage(Headers headers, Page page) {
        String link = headers.get("Link");
        if (TextUtils.isEmpty(link)) {
            page.setLast("").setLastPage(1).setNext("").setNextPage(1).setLast("").setLastPage(1);
            return;
        }
        String[] parts = StringUtils.split(link, ",");
        for (String part : parts) {
            PaginationLink bottomPaginationLink = new PaginationLink(part);
            if (bottomPaginationLink.rel == RelType.last) {
                page.setLast(bottomPaginationLink.uri.toString())
                        .setLastPage(bottomPaginationLink.page);
            } else if (bottomPaginationLink.rel == RelType.next) {
                page.setNext(bottomPaginationLink.uri.toString())
                        .setNextPage(bottomPaginationLink.page);
            } else if (bottomPaginationLink.rel == RelType.first) {
                page.setFirst(bottomPaginationLink.uri.toString())
                        .setFirstPage(bottomPaginationLink.page);
            }
        }
    }

}
