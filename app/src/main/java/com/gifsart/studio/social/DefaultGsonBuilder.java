package com.gifsart.studio.social;

import com.socialin.gson.FieldNamingPolicy;
import com.socialin.gson.Gson;
import com.socialin.gson.GsonBuilder;

public class DefaultGsonBuilder {
	
	private static Gson defaultGson = null;
	
	public static Gson getDefaultGson(){
		
		if(defaultGson == null)
			defaultGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();  //yyyy-MM-dd HH:mm:ss
		return defaultGson;
	}

}
