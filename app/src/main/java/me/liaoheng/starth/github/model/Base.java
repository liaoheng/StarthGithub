package me.liaoheng.starth.github.model;

import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 * @author liaoheng
 * @version 2016-06-25 16:37
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Base implements Serializable {
    private String message;
    private String documentation_url;

    public boolean isError() {
        return !TextUtils.isEmpty(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDocumentation_url() {
        return documentation_url;
    }

    public void setDocumentation_url(String documentation_url) {
        this.documentation_url = documentation_url;
    }
}
