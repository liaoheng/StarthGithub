package me.liaoheng.starth.github.util;

import com.github.liaoheng.common.network.OkHttp3Utils;
import com.github.liaoheng.common.util.NetException;
import com.github.liaoheng.common.util.NetServerException;

import java.io.IOException;

import me.liaoheng.starth.github.BuildConfig;
import me.liaoheng.starth.github.model.UserLogin;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author liaoheng
 * @version 2016-06-24 21:01
 */
public class RetrofitUtils {
    private RetrofitUtils() {
    }

    private static RetrofitUtils retrofitUtils;

    public static RetrofitUtils get() {
        if (retrofitUtils == null) {
            retrofitUtils = new RetrofitUtils();
        }
        return retrofitUtils;
    }

    private Retrofit mRetrofit;

    public void init() {
        OkHttp3Utils.Init init = OkHttp3Utils.init().setDefaultCache(Constants.IMAGE_DISK_CACHE_SIZE)
                .addHeaders("Accept", "application/vnd.github.v3.json")
                .addHeaders("Content-Type", "application/json; charset=utf-8")
                .addHeaders("User-Agent", "starth_github_version=" + BuildConfig.VERSION_NAME);
        if (UserLogin.get().isLogin()) {
            init.addHeaders(UserLogin.get().getAccessTokenHeaderKey(),
                    UserLogin.get().getAccessTokenHeaderContent());
        }
        init.setErrorHandleListener(new OkHttp3Utils.ErrorHandleListener() {
            @Override
            public Response checkError(Response response) throws NetException {
                if (!response.isSuccessful()) {
                    if (response.code() == 404) {
                        return response;
                    }
                    String string = "code : " + response.code();
                    if (response.body().contentLength() > 0) {
                        try {
                            string = response.body().string();
                        } catch (IOException ignored) {
                        }
                    }
                    throw new NetServerException(response.message(), string);
                }
                return response;
            }
        }).initialization();

        mRetrofit = new Retrofit.Builder().baseUrl(Constants.BASE_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(OkHttp3Utils.get().getClient()).build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
