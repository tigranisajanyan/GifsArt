package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gifsart.studio.R;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.GifsArtConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button loginButton;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button signInButton;

    private String userName;
    private String password;

    private CallbackManager callbackManager;

    public String fbId;
    public String fbEmail;
    public String fbUrl;
    public String fbToken;
    public String fbName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (Button) findViewById(R.id.login_button);
        userNameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        signInButton = (Button) findViewById(R.id.signup_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (checkEnteredUserName(userName) || checkEnteredPassword(password)) {
                    final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(SignInActivity.this);
                    animatedProgressDialog.show();
                    final UserContraller userContraller = new UserContraller(SignInActivity.this);
                    userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                        @Override
                        public void onRequestReady(int requestNumber, String messege) {
                            if (requestNumber == RequestConstants.LOGIN_USER_SUCCESS_CODE) {
                                editor = sharedPreferences.edit();
                                editor.putString("user_api_key", userContraller.getUserApiKey());
                                editor.putString("user_id", userContraller.getUserId());
                                editor.commit();
                                Intent intent = new Intent();
                                intent.putExtra("api_key", userContraller.getUserApiKey());
                                intent.putExtra("name", userContraller.getUserName());
                                intent.putExtra("photo_url", userContraller.getUserPhotoUrl());
                                setResult(RESULT_OK, intent);
                            }
                            if (requestNumber == RequestConstants.LOGIN_USER_ERROR_CODE) {
                                setResult(RESULT_CANCELED);
                            }
                            finish();
                            animatedProgressDialog.dismiss();
                        }
                    });
                    userContraller.loginUser(userNameEditText.getText().toString(), passwordEditText.getText().toString());
                } else {
                    Toast.makeText(SignInActivity.this, "wrong username and/or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*loginButton.setReadPermissions(Arrays.asList("public_profile,email,user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /*for (Iterator<String> it = loginResult.getRecentlyGrantedPermissions().iterator(); it.hasNext(); ) {
                    String f = it.next();
                    Log.d("gaga", f);
                }
                requestFacebookUserInfo(loginResult);
            }

            @Override
            public void onCancel() {
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });*/
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFacebookUserInfo(loginResult);
                return;
            }

            @Override
            public void onCancel() {
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoggedIn()) {
                    LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile,email,user_friends"));
                    return;
                } else {
                    LoginManager.getInstance().logOut();
                    return;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void requestFacebookUserInfo(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.d("request_facebook_user", object.toString());
                            fbId = object.getString("id");
                            fbName = object.getString("name");
                            fbToken = loginResult.getAccessToken().getToken();
                            try {
                                fbEmail = object.getString("email");
                            } catch (Exception e) {
                                fbEmail = "";
                            }
                            fbUrl = object.getString("link");

                            final UserContraller userContraller = new UserContraller(SignInActivity.this);
                            userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                                @Override
                                public void onRequestReady(int requestNumber, String messege) {
                                    if (messege.contains("error")) {
                                        UserContraller userContraller1 = new UserContraller(SignInActivity.this);
                                        userContraller1.setOnRequestReadyListener(new UserContraller.UserRequest() {
                                            @Override
                                            public void onRequestReady(int requestNumber, String messege) {
                                                if (requestNumber == RequestConstants.LOGIN_USER_SUCCESS_CODE) {
                                                    editor = sharedPreferences.edit();
                                                    editor.putString("user_api_key", userContraller.getUserApiKey());
                                                    editor.commit();
                                                    Intent intent = new Intent();
                                                    intent.putExtra("api_key", userContraller.getUserApiKey());
                                                    intent.putExtra("name", userContraller.getUserName());
                                                    intent.putExtra("photo_url", userContraller.getUserPhotoUrl());
                                                    setResult(RESULT_OK, intent);
                                                }
                                                if (requestNumber == RequestConstants.LOGIN_USER_ERROR_CODE) {
                                                    setResult(RESULT_CANCELED);
                                                }
                                                finish();
                                            }
                                        });
                                        userContraller1.loginUserWithFacebook(fbToken, getSignInParams(fbName, fbId, fbEmail, fbUrl, fbToken));
                                    }
                                }
                            });
                            userContraller.signUpWithFacebook(fbName, fbId, fbEmail, fbUrl, fbToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static String getSignInParams(final String name, final String userId, final String email, final String profileUrl, final String fbToken) {
        JSONObject authJson = new JSONObject();
        try {
            authJson.put("id", userId);
            authJson.put("profile_url", profileUrl);
            authJson.put("name", name);
            authJson.put("username", name);
            authJson.put("email", email);
            authJson.put("token", AccessToken.getCurrentAccessToken().getToken());
            authJson.put("token_expired", AccessToken.getCurrentAccessToken().getExpires().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return authJson.toString();
    }

    public boolean isLoggedIn() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkEnteredUserName(String userName) {
        if (userName.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEnteredPassword(String password) {
        if (password.equals("")) {
            return false;
        } else {
            return true;
        }
    }
}
