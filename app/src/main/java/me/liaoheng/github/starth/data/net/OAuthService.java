package me.liaoheng.github.starth.data.net;

import me.liaoheng.github.starth.model.AccessToken;
import me.liaoheng.github.starth.model.RequestToken;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author liaoheng
 * @version 2016-06-24 20:38
 */
public interface OAuthService {

    @Headers("Accept: application/json")
    @POST("https://github.com/login/oauth/access_token") Observable<AccessToken> getToken(
            @Body RequestToken requestToken);
}
