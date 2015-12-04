package com.gifsart.studio.social;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tigran on 12/2/15.
 */
public class ErrorHandler {

    public static boolean statusIsError(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.toString());
            if (jsonObject.getString("status").equals("error")) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean statusIsError(JSONObject response) {
        try {
            if (response.getString("status").equals("error")) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Intent createErrorMessege(String response) {
        JSONObject jsonObject = null;
        String reason = null;
        try {
            jsonObject = new JSONObject(response.toString());
            if (jsonObject.getString("status").equals("error")) {
                try {
                    reason = jsonObject.getString("reason");
                } catch (Exception e) {
                    reason = jsonObject.getString("message");
                } finally {
                    if (reason == null) {
                        reason = "error";
                    }
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            reason = response;
        }
        reason = reason.replaceAll("_", " ").toLowerCase();
        String output = reason.substring(0, 1).toUpperCase() + reason.substring(1);
        Intent intent = new Intent();
        intent.putExtra("error_messege", output);
        return intent;
    }

    public static String getErrorMessege(String messege) {
        JSONObject jsonObject = null;
        String reason = null;
        try {
            jsonObject = new JSONObject(messege.toString());
            if (jsonObject.getString("status").equals("error")) {
                try {
                    reason = jsonObject.getString("reason");
                } catch (Exception e) {
                    reason = jsonObject.getString("message");
                } finally {
                    if (reason == null) {
                        reason = "error";
                        return reason;
                    }
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            return messege;
        }
        reason = reason.replaceAll("_", " ").toLowerCase();
        String output = reason.substring(0, 1).toUpperCase() + reason.substring(1);
        return output;
    }

    public static boolean userAlreadyExistsError(String response) {
        if (response.contains("user_doesnt_exist")) {
            return true;
        } else {
            return false;
        }
    }

    public static void evaluateFacebookErrorResponse(Activity activity, FacebookRequestError error) {
        int errorCode = error.getErrorCode();
        if (errorCode >= 200 && errorCode <= 299) {
            Toast.makeText(activity, "permission error", Toast.LENGTH_LONG).show();
            activity.finish();
        } else if (errorCode == 102 || (error.getException() != null && error.getException().getMessage().contains("OAuthException"))) {
            Toast.makeText(activity, "access token expired", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
            activity.finish();
        }
    }

}
