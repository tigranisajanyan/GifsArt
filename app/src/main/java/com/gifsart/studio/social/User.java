package com.gifsart.studio.social;

import com.socialin.gson.annotations.Expose;
import com.socialin.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class serves as POJO
 * <p/>
 * <p>Some Getters of this class  throw a <tt>NullPointerException</tt>
 * if the collections or class objects provided to them are null.
 * <p/>
 * <p>This class is a member of the
 * <a href="www.com.picsart.com">
 * </a>.
 */
public class User implements Serializable {

    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;
    @Expose
    private List<String> tags = new ArrayList<String>();
    @SerializedName("is_verified")
    @Expose
    private Boolean isVerified;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("locations_count")
    @Expose
    private Integer locationsCount;
    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("following_count")
    @Expose
    private Integer followingCount;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("streams_count")
    @Expose
    private Integer streamsCount;

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("cover")
    @Expose
    private String cover;

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mature")
    @Expose
    private Boolean mature;
    @SerializedName("photos_count")
    @Expose
    private Integer photosCount;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("followers_count")
    @Expose
    private Integer followersCount;
    @SerializedName("username_changed")
    @Expose
    private Boolean usernameChanged;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("tags_count")
    @Expose
    private Integer tagsCount;

    @SerializedName("link")
    @Expose
    private String link;


    public User(String id) {
        this.id = Long.parseLong(id);
    }

    public User(String id, String name, String username, String photo, String cover, int followingCount, int followersCownt, int likesCount, int photosCount) {

        this.id = Long.parseLong(id);
        this.name = name;
        this.username = username;
        this.photo = photo;
        this.cover = cover;
        this.followingCount = followingCount;
        this.followersCount = followersCownt;
        this.likesCount = likesCount;
        this.photosCount = photosCount;

    }

    /**
     * @return The likesCount
     */
    public Integer getLikesCount() {
        return likesCount;
    }

    /**
     * @param likesCount The likes_count
     */
    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    /**
     * @return The tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @param tags The tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * @return The isVerified
     */
    public Boolean getIsVerified() {
        return isVerified;
    }

    /**
     * @param isVerified The is_verified
     */
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The locationsCount
     */
    public Integer getLocationsCount() {
        return locationsCount;
    }

    /**
     * @param locationsCount The locations_count
     */
    public void setLocationsCount(Integer locationsCount) {
        this.locationsCount = locationsCount;
    }

    /**
     * @return The statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage The status_message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @return The provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider The provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return The followingCount
     */
    public Integer getFollowingCount() {
        return followingCount;
    }

    /**
     * @param followingCount The following_count
     */
    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    /**
     * @return The photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * @param photo The photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * @return The streamsCount
     */
    public Integer getStreamsCount() {
        return streamsCount;
    }

    /**
     * @param streamsCount The streams_count
     */
    public void setStreamsCount(Integer streamsCount) {
        this.streamsCount = streamsCount;
    }

    /**
     * @return The id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = Long.parseLong(id);
    }

    /**
     * @return The cover
     */
    public String getCover() {
        return cover;
    }

    /**
     * @param cover The cover
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The mature
     */
    public Boolean getMature() {
        return mature;
    }

    /**
     * @param mature The mature
     */
    public void setMature(Boolean mature) {
        this.mature = mature;
    }

    /**
     * @return The photosCount
     */
    public Integer getPhotosCount() {
        return photosCount;
    }

    /**
     * @param photosCount The photos_count
     */
    public void setPhotosCount(Integer photosCount) {
        this.photosCount = photosCount;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The followersCount
     */
    public Integer getFollowersCount() {
        return followersCount;
    }

    /**
     * @param followersCount The followers_count
     */
    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    /**
     * @return The usernameChanged
     */
    public Boolean getUsernameChanged() {
        return usernameChanged;
    }

    /**
     * @param usernameChanged The username_changed
     */
    public void setUsernameChanged(Boolean usernameChanged) {
        this.usernameChanged = usernameChanged;
    }

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return The tagsCount
     */
    public Integer getTagsCount() {
        return tagsCount;
    }

    /**
     * @param tagsCount The tags_count
     */
    public void setTagsCount(Integer tagsCount) {
        this.tagsCount = tagsCount;
    }

    /**
     * @return
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "id: " + id + "\tname:  " + name + "\tusername:  " + username + "\tlink:  " + link + "\tstatus:  " + status + "\tphoto:  " +
                photo + "\tfollowing count:  " + followersCount + "\tfollowers count:  " + followersCount +
                "\tuser likes count:  " + likesCount + "\temail:  " + email + "\tkey:  " + key;

    }
}