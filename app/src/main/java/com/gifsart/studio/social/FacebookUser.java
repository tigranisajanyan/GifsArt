package com.gifsart.studio.social;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookUser {

	String id;
	String email;
	String link;
	String name;
    String profilePicture;

	FacebookCover cover;

	public class FacebookCover {
		String id;
		String source;
		int offsetY = 0;
		int userId;

		FacebookCover() {
		}

		public FacebookCover(JSONObject data) {
			if (data == null) return;
			JSONObject json = data.optJSONObject("cover");
			if (json != null) {
				id = json.optString("id");
				source = json.optString("source");
				offsetY = json.optInt("offset_y");
			}
			userId = data.optInt("id");
		}

		public JSONObject toJson() {
			JSONObject coverJson = new JSONObject();
			try {
				coverJson.put("id", id);
				coverJson.put("source", source);
				coverJson.put("offset_y", offsetY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return coverJson;
		}
	}

	public FacebookUser(JSONObject json) {
		if (json == null) return;

		try {
			id = json.optString("id");
            profilePicture = FacebookConstants.FB_GRAPH_SECURE_URL + id + "/picture" + "?type=normal";
			email = json.optString("email");
			link = json.optString("link");
			name = json.optString("name");

			cover = new FacebookCover(json);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", id);
			json.put("email", email);
			json.put("link", link);
			json.put("name", name);
			if (cover != null) json.put("cover", cover.toJson());

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return json;

	}

	public FacebookCover getCover() {
		return cover;
	}

    public String getProfilePicture() {
        return profilePicture;
    }
	public void setCover(FacebookCover newCover) {
		cover = newCover;
	}

	public void setCover(JSONObject json) {
		cover = new FacebookCover(json);
	}

	public String getCoverUrl() {
		return cover == null ? "" : cover.source;
	}

	public void setCoverUrl(String realUrl) {
		if (cover != null) {
			cover.source = realUrl;
		}
	}

	public String getCoverRedirect() {
		return cover == null ? "" : FacebookConstants.FB_GRAPH_SECURE_URL + cover.id + "/picture?access_token=" + AccessToken.getCurrentAccessToken().getToken() + "&redirect=false";

	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
