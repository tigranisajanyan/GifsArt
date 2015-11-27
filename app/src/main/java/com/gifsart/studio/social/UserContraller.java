package com.gifsart.studio.social;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tigran on 11/24/15.
 */
public class UserContraller {

    private Context context;

    private static final String API_KEY = "c985ce04-f6dd-448e-95f2-6a58d0d50431";

    private static final String USER_PROFILE_PICTURE_URL = "https://api.picsart.com/users/show/me.json?key=";
    private static final String USER_LOGIN_URL = "https://api.picsart.com/users/signin.json";
    private static final String USER_SIGNUP_URL = "https://api.picsart.com/users/signup.json";
    private static final String USER_UPDATE_INFO = "https://api.picsart.com/users/update.json?key=";

    private UserRequest userRequest;


    private String name;
    private String userId;
    private String userApiKey;
    private String userPictureUrl;

    public static final String LOG_TAG = "user_contraller";

    public UserContraller(Context context) {
        this.context = context;
    }

    /**
     *
     */
    public synchronized void requestUser(final String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, USER_PROFILE_PICTURE_URL + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "request_user: " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.toString());
                            userPictureUrl = jsonObject.getString("photo");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        userRequest.onRequestReady(RequestConstants.REQUEST_USER_SUCCESS_CODE, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                userRequest.onRequestReady(RequestConstants.REQUEST_USER_ERROR_CODE, error.getMessage());
            }
        });
        queue.add(stringRequest);
    }


    /**
     * @param userName
     * @param password
     */
    public synchronized void loginUser(final String userName, final String password) {
        String url = USER_LOGIN_URL;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(LOG_TAG, "login_user: " + response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    if (jsonObject.getString("status").equals("success")) {
                        userPictureUrl = jsonObject.getString("photo");
                        userApiKey = jsonObject.getString("key");
                        userId = jsonObject.getString("id");
                        name = userName;
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_SUCCESS_CODE, response);
                    } else {
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider", "site");
                params.put("username", userName);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    public synchronized void loginUserWithFacebook(final String token, final String jsonUser) {
        String url = USER_LOGIN_URL;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG, "login_user_facebook: " + response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    if (jsonObject.getString("status").equals("success")) {
                        userPictureUrl = jsonObject.getString("photo");
                        userApiKey = jsonObject.getString("key");
                        userId = jsonObject.getString("id");
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_SUCCESS_CODE, response);
                    } else {
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider", "facebook");
                params.put("username", token);
                params.put("password", jsonUser);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }


    /**
     * @param userName
     * @param password
     * @param email
     */
    public synchronized void signUpToPicsArt(final String userName, final String password, final String email) {
        String url = USER_SIGNUP_URL;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG, "sign_up: " + response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    if (jsonObject.getString("status").equals("success")) {
                        userPictureUrl = jsonObject.getString("photo");
                        userApiKey = jsonObject.getString("key");
                        userId = jsonObject.getString("id");
                        name = userName;
                        userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_SUCCESS_CODE, response);
                    } else {
                        userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_ERROR_CODE, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_ERROR_CODE, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider", "site");
                params.put("username", userName);
                params.put("name", userName);
                params.put("password", password);
                params.put("email", email);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    /**
     * @param name
     * @param userId
     * @param email
     * @param profileUrl
     * @param token
     */
    public synchronized void signUpWithFacebook(final String name, final String userId, final String email, final String profileUrl, final String token) {
        String url = USER_SIGNUP_URL;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG, "sign_up_facebook: " + response);
                if (!response.contains("error")) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.toString());
                        if (jsonObject.getString("status").equals("success")) {
                            userPictureUrl = jsonObject.getString("photo");
                            userApiKey = jsonObject.getString("key");
                            //name = userName;
                            userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_SUCCESS_CODE, response);
                        } else {
                            userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_ERROR_CODE, response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_SUCCESS_CODE, response);
                    //Log.d("gagag", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_ERROR_CODE, error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provider", "facebook");
                params.put("fb_token", token);
                params.put("fb_id", userId);
                params.put("fb_name", name);
                params.put("fb_url", profileUrl);
                params.put("fb_email", email);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }


    /**
     * @param name
     */
    public synchronized void uploadUserInfo(final String name) {
        String url = USER_UPDATE_INFO + API_KEY;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                userRequest.onRequestReady(RequestConstants.UPLOAD_USER_INFO_SUCCESS_CODE, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userRequest.onRequestReady(RequestConstants.UPLOAD_USER_INFO_ERROR_CODE, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provider", "site");
                params.put("name", name);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    public synchronized void uploadPhoto(final String filePath) {

        String url = USER_UPDATE_INFO + API_KEY;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                userRequest.onRequestReady(RequestConstants.UPLOAD_USER_PHOTO_SUCCESS_CODE, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userRequest.onRequestReady(RequestConstants.UPLOAD_USER_PHOTO_ERROR_CODE, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provider", "site");
                params.put("photo", filePath);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }


    public String getUserApiKey() {
        return userApiKey;
    }

    public String getUserName() {
        return name;
    }

    public String getUserPhotoUrl() {
        return userPictureUrl;
    }

    public String getUserId() {
        return userId;
    }


    public void setOnRequestReadyListener(UserRequest userRequest) {
        this.userRequest = userRequest;
    }

    public interface UserRequest {
        void onRequestReady(int requestNumber, String messege);
    }


}
