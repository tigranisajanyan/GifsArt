package com.gifsart.studio.social;

/**
 * Created by Tigran on 11/27/15.
 */
public class User {

    private String userId;
    private String email;
    private String userProfileUrl;
    private String token;
    private String name;
    private String userProfilePictureUrl;
    private String userApiKey;
    private String userName;

    public User() {

    }

    public User(String userId, String email, String userProfileUrl, String token, String name) {
        this.userId = userId;
        this.email = email;
        this.userProfileUrl = userProfileUrl;
        this.token = token;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserProfilePictureUrl() {
        return userProfilePictureUrl;
    }

    public void setUserProfilePictureUrl(String userProfilePictureUrl) {
        this.userProfilePictureUrl = userProfilePictureUrl;
    }

    public String getUserApiKey() {
        return userApiKey;
    }

    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
