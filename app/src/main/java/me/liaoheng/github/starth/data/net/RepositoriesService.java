package me.liaoheng.github.starth.data.net;

import java.util.List;
import me.liaoheng.github.starth.model.Commits;
import me.liaoheng.github.starth.model.Repositories;
import me.liaoheng.github.starth.model.RepositoriesFileContent;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author liaoheng
 * @version 2016-07-19 11:51
 */
public interface RepositoriesService {

    @GET("repos/{owner}/{repo}/commits") Observable<Response<List<Commits>>> getRepositoriesCommits(
            @Path("owner") String owner, @Path("repo") String repo, @Query("page") long page);

    @GET("repos/{owner}/{repo}/issues") Observable<List<Commits>> getRepositoriesIssues(
            @Path("owner") String owner, @Path("repo") String repo);

    @GET("repos/{owner}/{repo}/contents") Observable<Repositories> getRepositoriesContents(
            @Path("owner") String owner, @Path("repo") String repo);


    @Headers("Accept: application/vnd.github.VERSION.raw") @GET Observable<ResponseBody> getRepositoriesFileContentRaw(
            @Url String url);

    @Headers("Accept: application/vnd.github.VERSION.html") @GET Observable<ResponseBody> getRepositoriesFileContentHtml(
            @Url String url);

    @GET("repos/{owner}/{repo}/contents/{pathName}") Observable<List<RepositoriesFileContent>> getRepositoriesPathContent(
            @Path("owner") String owner, @Path("repo") String repo,
            @Path("pathName") String pathName);

    @Headers("Accept: application/vnd.github.VERSION.html") @GET("repos/{owner}/{repo}/readme") Observable<ResponseBody> getRepositoriesReadMe(
            @Path("owner") String owner, @Path("repo") String repo);

}
