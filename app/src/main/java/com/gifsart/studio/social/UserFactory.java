package com.gifsart.studio.social;

import com.socialin.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class consists exclusively of parsing methods, that trying make User/ collection of User
 * using gson reflection on class.
 */
public class UserFactory {

    private UserFactory() {
    }

    /**
     * Tries to Parse Object to User
     *
     * @param object Object
     * @return User
     */
    public static User parseFromObject(Object object) {

        JSONObject jsonObject = (JSONObject) object;

        Gson gson = new Gson();
        User user = null;

        try {
            user = gson.fromJson(jsonObject.toString(), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Tries to Parse Object to User
     *
     * @return User
     */
    public static User parseFromString(String response) {

        Gson gson = new Gson();
        User user = null;

        try {
            user = gson.fromJson(response, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * @param o      Object
     * @param offset starting point (from)
     * @param limit  limit of outcome
     *               <p/>
     *               <p/>
     *               Tries to Parse Object to ArrayList of User instances
     */
    public static ArrayList<User> parseFromAsArray(Object o, int offset, int limit, String keyword) {

        ArrayList<User> userArrayList = new ArrayList<>();
        User nwUs = null;
        Gson gson;

        try {
            JSONObject jsonObj = (JSONObject) o;
            gson = new Gson();
            JSONArray jarr = null;
            if (keyword != null || keyword != "") {
                try {
                    jarr = new JSONArray(jsonObj.get(keyword).toString());
                } catch (Exception e) {
                }
                ;
            } else jarr = new JSONArray(jsonObj.toString());

            for (int i = offset; i < jarr.length() && limit >= 0; i++, limit--) {
                JSONObject jsonObject = jarr.getJSONObject(i);
                gson = new Gson();
                try {
                    nwUs = gson.fromJson(jsonObject.toString(), User.class);

                } catch (Exception e) {
                }

                userArrayList.add(nwUs);

            }

            return userArrayList;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param response
     * @param offset
     * @param limit
     * @return
     */
    public static ArrayList<Photo> parseFromStringAsPhotosArray(String response, int offset, int limit) {
        ArrayList<Photo> tmpPh = new ArrayList<>();
        Photo nwPh;
        User nwUs;
        if (offset < 0) offset = 0;
        if (limit < 1) limit = 1;

        try {
            JSONObject jsonObj = new JSONObject(response);
            Gson gson = new Gson();
            JSONArray jarr = jsonObj.getJSONArray("response");
            for (int i = offset; i < jarr.length() && limit > 0; i++, limit--) {
                nwPh = gson.fromJson(jarr.get(i).toString(), Photo.class);
                try {
                    gson = new Gson();
                    JSONObject jooobj = (JSONObject) jarr.get(i);
                    nwUs = gson.fromJson(jooobj.get("user").toString(), User.class);
                    nwPh.setOwner(nwUs);
                } catch (Exception e) {
                }
                tmpPh.add(nwPh);
            }
            return tmpPh;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
