package com.gifsart.studio.social;

import com.socialin.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class UserConnection {

	@SerializedName("provider")
	public String provider;

	@SerializedName("token")
	public String token;

	@SerializedName("id")
	public String connectionId;

	@SerializedName("data")
	public Data data = new Data();

    @SerializedName("settings")
    public Settings settings = new Settings();

    public class Settings {
        @SerializedName("enable_action_follow")
        public boolean actionFollow = true;

        @SerializedName("enable_action_add")
        public boolean actionAdd = true;

        @SerializedName("enable_action_comment")
        public boolean actionComment = true;

        @SerializedName("enable_action_like")
        public boolean actionLike = true;

        @SerializedName("enable_action_all")
        public boolean actionAll = true;
    }

	public class Data {
		@SerializedName("token")
		public String token;
		@SerializedName("token_secret")
		public String tokenSecret;
		@SerializedName("token_expired")
		public long tokenExpired = 0;
		@SerializedName("profile_url")
		public String profileUrl;
		@SerializedName("profile_img_url")
		public String profileImgUrl;
		@SerializedName("name")
		public String name;
		@SerializedName("screen_name")
		public String screenName;
		@SerializedName("email")
		public String email;
		@SerializedName("id")
		public String id;
		@SerializedName("cover")
		public String cover;

		public Data() {
		}
		
		public Data(JSONObject json) throws JSONException {
			if (json == null) return;
			token = json.optString("token");
			tokenSecret = json.optString("token_secret");
			tokenExpired = json.optLong("token_expired");
			profileUrl = json.optString("profile_url");
			profileImgUrl = json.optString("profile_img_url");
			name = json.optString("name");
			screenName = json.optString("screen_name");
			email = json.optString("email");
			id = json.optString("id");
			cover = json.optString("cover");
		}

		public JSONObject toJson() throws JSONException {
			String jsonStr = DefaultGsonBuilder.getDefaultGson().toJson(this);
			return new JSONObject(jsonStr);
		}

	}

}
