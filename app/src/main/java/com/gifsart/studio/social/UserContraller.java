package com.gifsart.studio.social;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gifsart.studio.R;
import com.gifsart.studio.utils.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tigran on 11/24/15.
 */
public class UserContraller {

    private Context context;

    public static final String LOG_TAG = "user_contraller";

    //private static final String API_KEY = "c985ce04-f6dd-448e-95f2-6a58d0d50431";

    private static final String USER_LOGIN_URL = "https://api.picsart.com/users/signin.json";
    private static final String USER_SIGNUP_URL = "https://api.picsart.com/users/signup.json";
    private static final String USER_UPDATE_INFO = "https://api.picsart.com/users/update.json?key=";
    private static final String USER_PROFILE_REQUEST = "https://api.picsart.com/users/show/me.json?key=";
    private static final String USER_PROFILE_PHOTOS = "https://api.picsart.com/photos/show/me.json?key=";
    private static final String USER_PROFILE_GIFS = "https://api.picsart.com/photos/search.json?key=";

    private static final String UPDATE_PHOTO_INFO = "https://api.picsart.com/photos/update/";
    private static final String REMOVE_USER_PHOTO = "https://api.picsart.com/photos/remove/";

    private static final String RESET_USER_PASSWORD = "https://api.picsart.com/users/reset.json";
    private static final String JSON_PREFIX = ".json";
    private static final String KEY_PREFIX = "?key=";
    private static final String SAVED_USER_FILENAME = "user.srl";

    private static final String LIMIT_PREFIX = "&limit=";
    private static final String OFFSET_PREFIX = "&offset=";

    private static final String LOCAL = "http://192.168.3.11:3000/api/users/signup.json";

    private UserRequest userRequest;

    private User user;
    private ArrayList<Photo> userPhotos;


    public UserContraller(Context context) {
        this.context = context;
    }

    /**
     *
     */
    public synchronized void requestUser(final String apiKey) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_PROFILE_REQUEST + apiKey;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(LOG_TAG, "request_user: " + response);
                            if (ErrorHandler.statusIsError(response)) {
                                userRequest.onRequestReady(RequestConstants.REQUEST_USER_ERROR_CODE, response);
                            } else {
                                user = UserFactory.parseFrom(response);
                                userRequest.onRequestReady(RequestConstants.REQUEST_USER_SUCCESS_CODE, response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.REQUEST_USER_ERROR_CODE, error.getMessage());
                }
            });
            queue.add(stringRequest);
        } else {
            userRequest.onRequestReady(RequestConstants.REQUEST_USER_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }


    /**
     * @param userName
     * @param password
     */
    public synchronized void loginUser(final String userName, final String password) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_LOGIN_URL;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "login_user: " + response);
                    if (ErrorHandler.statusIsError(response)) {
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, response);
                    } else {
                        user = UserFactory.parseFrom(response);
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_SUCCESS_CODE, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
        } else {
            userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }

    /**
     * @param token
     * @param jsonUser
     */
    public synchronized void loginUserWithFacebook(final String token, final String jsonUser) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_LOGIN_URL;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "login_user_facebook: " + response);
                    if (ErrorHandler.statusIsError(response)) {
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, response);
                    } else {
                        user = UserFactory.parseFrom(response);
                        userRequest.onRequestReady(RequestConstants.LOGIN_USER_SUCCESS_CODE, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("provider", "facebook");
                    params.put("token", token);
                    params.put("auth", jsonUser);
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
        } else {
            userRequest.onRequestReady(RequestConstants.LOGIN_USER_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }


    /**
     * @param userName
     * @param password
     * @param email
     */
    public synchronized void signUpToPicsArt(final String userName, final String password, final String email) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_SIGNUP_URL;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "sign_up: " + response);
                    if (ErrorHandler.statusIsError(response)) {
                        userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_ERROR_CODE, response);
                    } else {
                        user = UserFactory.parseFrom(response);
                        userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_SUCCESS_CODE, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
        } else {
            userRequest.onRequestReady(RequestConstants.SIGN_UP_PICSART_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }

    /**
     * @param facebookUser
     */
    public synchronized void signUpWithFacebook(final FacebookUser facebookUser) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_SIGNUP_URL; //LOCAL;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "sign_up_facebook: " + response);
                    if (ErrorHandler.statusIsError(response)) {
                        userRequest.onRequestReady(RequestConstants.SIGN_UP_WITH_FACEBOOK_ERROR_CODE, response);
                    } else {
                        user = UserFactory.parseFrom(response);
                        userRequest.onRequestReady(RequestConstants.SIGN_UP_WITH_FACEBOOK_SUCCESS_CODE, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.SIGN_UP_WITH_FACEBOOK_ERROR_CODE, error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("provider", "facebook");
                    params.put("fb_id", facebookUser.getId());
                    params.put("fb_email", facebookUser.getEmail());

                    params.put("email", facebookUser.getEmail());
                    params.put("name", facebookUser.getName());
                    params.put("username", facebookUser.getName());
                    params.put("cover", facebookUser.getCoverUrl());
                    params.put("photo", facebookUser.getProfilePicture());

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
        } else {
            userRequest.onRequestReady(RequestConstants.SIGN_UP_WITH_FACEBOOK_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }


    /**
     * @param name
     */
    public synchronized void uploadUserInfo(String apiKey, final String name, final String userName) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_UPDATE_INFO + apiKey;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "upload_info: " + response);
                    if (ErrorHandler.statusIsError(response)) {
                        userRequest.onRequestReady(RequestConstants.UPLOAD_USER_INFO_ERROR_CODE, response);
                    } else {
                        userRequest.onRequestReady(RequestConstants.UPLOAD_USER_INFO_SUCCESS_CODE, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.UPLOAD_USER_INFO_ERROR_CODE, error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("provider", "site");
                    params.put("name", name);
                    if (userName != "") {
                        params.put("username", userName);
                    }
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
        } else {
            userRequest.onRequestReady(RequestConstants.UPLOAD_USER_INFO_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }

    /**
     * Requests Photos of the User
     *
     * @param apiKey ID of the User
     * @param offset starting point
     * @param limit  limit of users
     *               <p/>
     *               onResponse 209 code will be called in listener
     *               onErrorResponse 309 code will be called in listener
     */
    public synchronized void requestUserPhotos(String apiKey, final int offset, final int limit) {
        if (Utils.haveNetworkConnection(context)) {
            String url = USER_PROFILE_GIFS + apiKey + OFFSET_PREFIX + offset + LIMIT_PREFIX + limit + "&photo_owner=1&recent=1&ext=gif";
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(LOG_TAG, "request_user_gifs: " + response);
                            if (ErrorHandler.statusIsError(response)) {
                                userRequest.onRequestReady(RequestConstants.REQUEST_USER_PHOTO_ERROR_CODE, response);
                            } else {
                                userPhotos = UserFactory.parseFromArray(response, 0, 30);
                                userRequest.onRequestReady(RequestConstants.REQUEST_USER_PHOTO_SUCCESS_CODE, response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.REQUEST_USER_PHOTO_ERROR_CODE, error.getMessage());
                }
            });
            queue.add(stringRequest);
        } else {
            userRequest.onRequestReady(RequestConstants.REQUEST_USER_PHOTO_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }

    /**
     * @param email
     */
    public synchronized void resetUserPassword(final String email) {
        if (Utils.haveNetworkConnection(context)) {
            String url = RESET_USER_PASSWORD;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "reset_password: " + response);
                    if (ErrorHandler.statusIsError(response)) {
                        userRequest.onRequestReady(RequestConstants.REQUEST_RESET_PASSWORD_ERROR_CODE, response);
                    } else {
                        userRequest.onRequestReady(RequestConstants.REQUEST_RESET_PASSWORD_SUCCESS_CODE, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.REQUEST_RESET_PASSWORD_ERROR_CODE, error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user", email);
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
        } else {
            userRequest.onRequestReady(RequestConstants.REQUEST_RESET_PASSWORD_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }


    /**
     * @param photoId
     */
    public synchronized void updatePhotoInfo(final String photoId) {
        if (Utils.haveNetworkConnection(context)) {
            String url = UPDATE_PHOTO_INFO + photoId + JSON_PREFIX + KEY_PREFIX + readUserFromFile(context).getKey();
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "update_photo_info: " + response);
                    userRequest.onRequestReady(RequestConstants.UPDATE_PHOTO_INFO_SUCCESS_CODE, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.UPDATE_PHOTO_INFO_ERROR_CODE, error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("is_public", "1");
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
        } else {
            userRequest.onRequestReady(RequestConstants.UPDATE_PHOTO_INFO_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }

    /**
     * @param photoId
     * @param apiKey
     */
    public synchronized void removeUserPhoto(final String photoId, final String apiKey) {
        if (Utils.haveNetworkConnection(context)) {
            String url = REMOVE_USER_PHOTO + photoId + JSON_PREFIX + KEY_PREFIX + apiKey;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, "remove_photo: " + response);
                    userRequest.onRequestReady(RequestConstants.REMOVE_PHOTO_SUCCESS_CODE, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userRequest.onRequestReady(RequestConstants.REMOVE_PHOTO_ERROR_CODE, error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
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
        } else {
            userRequest.onRequestReady(RequestConstants.REMOVE_PHOTO_ERROR_CODE, context.getString(R.string.no_internet_connection));
        }
    }


    public synchronized void upload12(String filePath, String apiKey) {

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Photo> getUserPhotos() {
        return userPhotos;
    }

    public void setUserPhotos(ArrayList<Photo> userPhotos) {
        this.userPhotos = userPhotos;
    }

    public interface UserRequest {
        void onRequestReady(int requestNumber, String messege);
    }

    public void setOnRequestReadyListener(UserRequest userRequest) {
        this.userRequest = userRequest;
    }


    public static AlertDialog.Builder setupDialogBuilder(Context context, String messege) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messege)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder;
    }

    /**
     * @param context
     * @param user
     */
    public static void writeUserToFile(Context context, User user) {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            fos = context.openFileOutput(SAVED_USER_FILENAME, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(user);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param context
     * @return
     */
    public static User readUserFromFile(Context context) {
        FileInputStream fis = null;
        User user = null;
        try {
            fis = context.openFileInput(SAVED_USER_FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            user = (User) is.readObject();
            is.close();
            fis.close();
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private static void saveUserData() {
        SharedPreferences user_prefs = getPrefs();
        if (user_prefs != null && user != null) {
            SharedPreferences.Editor editor = user_prefs.edit();
            editor.putString(SESSION_USER_DATA, user.toJson().toString());
            editor.apply();
        }
    }

    private static FacebookUser retrieveUserData() {
        try {
            String jsonData = getPrefs().getString(SESSION_USER_DATA, null);
            if (jsonData != null) {
                return new FacebookUser(new JSONObject(jsonData));
            }
        } catch (JSONException e) {
            L.w(TAG, "retrieveUserData", e);
        }
        return null;
    }*/

}
