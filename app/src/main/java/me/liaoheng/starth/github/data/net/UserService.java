package me.liaoheng.starth.github.data.net;

import java.util.List;
import me.liaoheng.starth.github.model.Followers;
import me.liaoheng.starth.github.model.Following;
import me.liaoheng.starth.github.model.Repositories;
import me.liaoheng.starth.github.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author liaoheng
 * @version 2016-06-25 01:06
 */
public interface UserService {

    @GET("users/{username}") Observable<User> getUser(@Path("username") String loginName);

    @GET("user") Call<User> getCurUserCall();

    @GET("users/{username}/starred") Observable<Response<List<Repositories>>> getUserStars(
            @Path("username") String loginName, @Query("page") long page);

    @GET("users/{username}/repos") Observable<Response<List<Repositories>>> getUserRepos(
            @Path("username") String loginName, @Query("page") long page);

    @GET("users/{username}/followers") Observable<Response<List<Followers>>> getUserFollowers(
            @Path("username") String loginName, @Query("page") long page);

    @GET("users/{username}/following") Observable<Response<List<Following>>> getUserFollowing(
            @Path("username") String loginName, @Query("page") long page);

    @GET("user/following/{username}") Call<ResponseBody> checkIsFollow(
            @Path("username") String loginName);

    @PUT("user/following/{username}") Call<ResponseBody> follow(@Path("username") String loginName);

    @DELETE("user/following/{username}") Call<ResponseBody> unFollow(
            @Path("username") String loginName);

}

