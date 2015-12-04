package com.gifsart.studio.social;


import com.socialin.gson.annotations.Expose;
import com.socialin.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This class serves as POJO
 *
 * <p>Some Getters of this class  throw a <tt>NullPointerException</tt>
 * if the collections or class objects provided to them are null.
 *
 * It uses enum type IS to differentiate cover/profile/general types of pictures.
 *
 * <p>This class is a member of the
 * <a href="www.com.picsart.com">
 * </a>.
 *
 * @author  Arman Andreasyan 2/23/15
 */


public class Photo {

    SimpleDateFormat sdf;
    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;

    @SerializedName("tags")
    @Expose
    private List<String> tags;

    @SerializedName("views_count")
    @Expose
    private Integer viewsCount;

    @SerializedName("comments_count")
    @Expose
    private Integer commentsCount;
    @Expose
    @SerializedName("status")
    private String status;

    @SerializedName("width")
    @Expose
    private Integer width;

    @SerializedName("streams_count")
    @Expose
    private Integer streamsCount;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("is_liked")
    @Expose
    private Boolean isLiked;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("is_reposted")
    @Expose
    private Boolean isReposted;

    @SerializedName("height")
    @Expose
    private Integer height;

    @SerializedName("created")
    @Expose
    private String created;

    private Date createdDate;

    @SerializedName("mature")
    @Expose
    private Boolean mature;

    @SerializedName("reposts_count")
    @Expose
    private Integer repostsCount;

    @SerializedName("public")
    @Expose
    private Boolean _public;

    @SerializedName("user")
    @Expose
    private User owner;

    @SerializedName("user_id")
    @Expose
    private String ownerID;

    IS isFor;
    private String path;

    /**
     * Used to differentiate Photo purpose.
     *
     * */
    public enum IS {AVATAR, COVER, GENERAL}


    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Integer getCommentsCount() {
        if(commentsCount==null)return 0;
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getStreamsCount() {
        return streamsCount;
    }

    public void setStreamsCount(Integer streamsCount) {
        this.streamsCount = streamsCount;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getIsReposted() {
        return isReposted;
    }

    public void setIsReposted(Boolean isReposted) {
        this.isReposted = isReposted;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Boolean getMature() {
        return mature;
    }

    public void setMature(Boolean mature) {
        this.mature = mature;
    }

    public Integer getRepostsCount() {
        return repostsCount;
    }

    public void setRepostsCount(Integer repostsCount) {
        this.repostsCount = repostsCount;
    }

    public Boolean getIsPublic() {
        return _public;
    }

    public void setPublic(Boolean _public) {
        this._public = _public;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }


    public IS getIsFor() {
        return isFor;
    }

    public void setIsFor(IS isFor) {
        this.isFor = isFor;
    }

    public List<String> getTags() {
        return tags;
    }

    public Integer getWidth() {
        return width;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHeight() {
        return height;
    }

    public Date getCreated() {
        if (createdDate == null) {
            try {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                return createdDate = sdf.parse(created);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return createdDate;
    }

    public User getOwner() {
        return owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOwnerID() {
        return ownerID;
    }



    public Photo(String id, String url, String title, ArrayList tags, String crrated, boolean isMature, int width, int height, int likesCount, int viewsCount, int commentsCount,
                 int repostsCount, boolean isLiked, boolean isReposted, String ownerID) {
        init(id, url, title, tags, crrated,
                isMature, width, height, likesCount, viewsCount, commentsCount, repostsCount, isLiked, isReposted, ownerID);
    }
    public Photo(String id, String url, String title, String crrated, String ownerid) {
        this.id = id;
        this.title = title;
        try {
            this.created = crrated;
            if (created != null) {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                this.createdDate = sdf.parse(created);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.ownerID = ownerid;
        this.url = url;
        this.tags = null;

    }
    public Photo(IS isFor) {
        this(null, null, null, null, null);
        this.isFor = isFor;

    }


    private void init(String id, String url, String title, ArrayList tags, String crrated, boolean isMature, int width, int height, int likesCount, int viewsCount,
                      int commentsCount, int repostsCount, boolean isLiked, boolean isReposted, String ownerID) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.tags = tags;

        try {
            this.created = crrated;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
            this.createdDate = sdf.parse(created);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mature = isMature;
        this.width = width;
        this.height = height;
        this.likesCount = likesCount;
        this.viewsCount = viewsCount;
        this.commentsCount = commentsCount;
        this.repostsCount = repostsCount;
        this.isLiked = isLiked;
        this.isReposted = isReposted;
        this.ownerID = ownerID;
    }


}




