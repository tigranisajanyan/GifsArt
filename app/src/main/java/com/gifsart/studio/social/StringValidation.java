package com.gifsart.studio.social;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.gifsart.studio.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tigran on 12/3/15.
 */
public class StringValidation {

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean emailValidation(Context context, String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        } else {
            AlertDialog alert1 = UserContraller.setupDialogBuilder(context, context.getString(R.string.alert_invalid_email)).create();
            alert1.show();
        }
        return isValid;
    }

    /**
     * @param password
     * @return
     */
    public static boolean passwordValidation(Context context, String password) {
        boolean isValid = false;

        String expression = "^\\S{6,20}$";
        CharSequence inputStr = password;

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        } else {
            AlertDialog alert1 = UserContraller.setupDialogBuilder(context, context.getString(R.string.alert_invalid_password)).create();
            alert1.show();
        }
        return isValid;
    }

    /**
     * @param username
     * @return
     */
    public static boolean usernameValidation(Context context, String username) {
        boolean isValid = false;

        String expression = "^[a-z0-9_-]{3,15}$";
        CharSequence inputStr = username;

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        } else {
            AlertDialog alert1 = UserContraller.setupDialogBuilder(context, context.getString(R.string.alert_invalid_username)).create();
            alert1.show();
        }
        return isValid;

    }

}
