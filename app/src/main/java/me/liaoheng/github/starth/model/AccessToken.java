package me.liaoheng.github.starth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Access token info
 *
 * @author liaoheng
 * @version 2016-06-25 11:22:10
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class AccessToken {
    private String access_token;
    private String token_type;
    private String scope;
    private String error;
    private String error_description;
    private String error_uri;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getError_uri() {
        return error_uri;
    }

    public void setError_uri(String error_uri) {
        this.error_uri = error_uri;
    }

    @Override public String toString() {
        return "AccessToken{" +
               "access_token='" + access_token + '\'' +
               ", token_type='" + token_type + '\'' +
               ", scope='" + scope + '\'' +
               '}';
    }
}
