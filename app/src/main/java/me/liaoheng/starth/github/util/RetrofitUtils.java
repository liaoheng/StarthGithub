package me.liaoheng.starth.github.util;

import com.github.liaoheng.common.plus.util.OkHttp3Utils;
import me.liaoheng.starth.github.BuildConfig;
import me.liaoheng.starth.github.model.UserLogin;
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

    Retrofit mRetrofit;

    public void init() {
        OkHttp3Utils.get().addHeader("Accept", "application/vnd.github.v3.json");
        OkHttp3Utils.get().addHeader("Content-Type", "application/json; charset=utf-8");
        OkHttp3Utils.get()
                .addHeader("User-Agent", "starth_github_version=" + BuildConfig.VERSION_NAME);
        if (UserLogin.get().isLogin()) {
            OkHttp3Utils.get().addHeader(UserLogin.get().getAccessTokenHeaderKey(),
                    UserLogin.get().getAccessTokenHeaderContent());
        }

        mRetrofit = new Retrofit.Builder().baseUrl(Constants.BASE_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(OkHttp3Utils.get().getClient()).build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
