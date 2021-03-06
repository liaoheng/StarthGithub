package me.liaoheng.starth.github.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 * @author liaoheng
 * @version 2016-06-25 01:20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable  {
    private String login;
    private int     id;
    private String  avatar_url;
    private String  gravatar_id;
    private String  url;
    private String  html_url;
    private String  followers_url;
    private String  following_url;
    private String  gists_url;
    private String  starred_url;
    private String  subscriptions_url;
    private String  organizations_url;
    private String  repos_url;
    private String  events_url;
    private String  received_events_url;
    private String  type;
    private boolean site_admin;
    private String  name;
    private String  company;
    private String  blog;
    private String  location;
    private String  email;
    private boolean hireable;
    private String  bio;
    private int     public_repos;
    private int     public_gists;
    private int     followers;
    private int     following;
    private String  created_at;
    private String  updated_at;
    private int     total_private_repos;
    private int     owned_private_repos;
    private int     private_gists;
    private int     disk_usage;
    private int     collaborators;

    @JsonIgnore
    private boolean isFollow;

    public User() {
    }

    public User(User user) {
        avatar_url = user.getAvatar_url();
        login = user.getLogin();
        name = user.getName();
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public int getId() {
        return id;
    }
    public String getStringId(){
        return String.valueOf(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getGravatar_id() {
        return gravatar_id;
    }

    public void setGravatar_id(String gravatar_id) {
        this.gravatar_id = gravatar_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getFollowers_url() {
        return followers_url;
    }

    public void setFollowers_url(String followers_url) {
        this.followers_url = followers_url;
    }

    public String getFollowing_url() {
        return following_url;
    }

    public void setFollowing_url(String following_url) {
        this.following_url = following_url;
    }

    public String getGists_url() {
        return gists_url;
    }

    public void setGists_url(String gists_url) {
        this.gists_url = gists_url;
    }

    public String getStarred_url() {
        return starred_url;
    }

    public void setStarred_url(String starred_url) {
        this.starred_url = starred_url;
    }

    public String getSubscriptions_url() {
        return subscriptions_url;
    }

    public void setSubscriptions_url(String subscriptions_url) {
        this.subscriptions_url = subscriptions_url;
    }

    public String getOrganizations_url() {
        return organizations_url;
    }

    public void setOrganizations_url(String organizations_url) {
        this.organizations_url = organizations_url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String getEvents_url() {
        return events_url;
    }

    public void setEvents_url(String events_url) {
        this.events_url = events_url;
    }

    public String getReceived_events_url() {
        return received_events_url;
    }

    public void setReceived_events_url(String received_events_url) {
        this.received_events_url = received_events_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSite_admin() {
        return site_admin;
    }

    public void setSite_admin(boolean site_admin) {
        this.site_admin = site_admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHireable() {
        return hireable;
    }

    public void setHireable(boolean hireable) {
        this.hireable = hireable;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getPublic_repos() {
        return public_repos;
    }

    public void setPublic_repos(int public_repos) {
        this.public_repos = public_repos;
    }

    public int getPublic_gists() {
        return public_gists;
    }

    public void setPublic_gists(int public_gists) {
        this.public_gists = public_gists;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getTotal_private_repos() {
        return total_private_repos;
    }

    public void setTotal_private_repos(int total_private_repos) {
        this.total_private_repos = total_private_repos;
    }

    public int getOwned_private_repos() {
        return owned_private_repos;
    }

    public void setOwned_private_repos(int owned_private_repos) {
        this.owned_private_repos = owned_private_repos;
    }

    public int getPrivate_gists() {
        return private_gists;
    }

    public void setPrivate_gists(int private_gists) {
        this.private_gists = private_gists;
    }

    public int getDisk_usage() {
        return disk_usage;
    }

    public void setDisk_usage(int disk_usage) {
        this.disk_usage = disk_usage;
    }

    public int getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(int collaborators) {
        this.collaborators = collaborators;
    }

    @Override public String toString() {
        return "User{" +
               "login='" + login + '\'' +
               ", id=" + id +
               ", avatar_url='" + avatar_url + '\'' +
               ", gravatar_id='" + gravatar_id + '\'' +
               ", url='" + url + '\'' +
               ", html_url='" + html_url + '\'' +
               ", followers_url='" + followers_url + '\'' +
               ", following_url='" + following_url + '\'' +
               ", gists_url='" + gists_url + '\'' +
               ", starred_url='" + starred_url + '\'' +
               ", subscriptions_url='" + subscriptions_url + '\'' +
               ", organizations_url='" + organizations_url + '\'' +
               ", repos_url='" + repos_url + '\'' +
               ", events_url='" + events_url + '\'' +
               ", received_events_url='" + received_events_url + '\'' +
               ", type='" + type + '\'' +
               ", site_admin=" + site_admin +
               ", name='" + name + '\'' +
               ", company='" + company + '\'' +
               ", blog='" + blog + '\'' +
               ", location='" + location + '\'' +
               ", email='" + email + '\'' +
               ", hireable=" + hireable +
               ", bio='" + bio + '\'' +
               ", public_repos=" + public_repos +
               ", public_gists=" + public_gists +
               ", followers=" + followers +
               ", following=" + following +
               ", created_at='" + created_at + '\'' +
               ", updated_at='" + updated_at + '\'' +
               ", total_private_repos=" + total_private_repos +
               ", owned_private_repos=" + owned_private_repos +
               ", private_gists=" + private_gists +
               ", disk_usage=" + disk_usage +
               ", collaborators=" + collaborators +
               '}';
    }
}
