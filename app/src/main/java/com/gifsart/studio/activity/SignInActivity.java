package com.gifsart.studio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gifsart.studio.R;
import com.gifsart.studio.social.ErrorHandler;
import com.gifsart.studio.social.FacebookConstants;
import com.gifsart.studio.social.FacebookUser;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.StringValidation;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity implements FacebookConstants {

    private static final String LOG_TAG = "signin_activity";
    private static final int REQUEST_PERSONALIZE_USER_ACTIVITY = 555;
    private static final int REQUEST_RESET_PASSWORD_ACTIVITY = 777;

    private SignInActivity context = this;

    private Button facebookLoginButton;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button signInButton;

    private String userName;
    private String password;

    private CallbackManager callbackManager;
    private String fbToken;

    private static FacebookUser fbUser;

    private LoginManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        callbackManager = CallbackManager.Factory.create();


        facebookLoginButton = (Button) findViewById(R.id.facebook_login_button);
        userNameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        signInButton = (Button) findViewById(R.id.signin_button);

        manager = LoginManager.getInstance();

        manager.logOut();

        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFacebookUserInfo(loginResult);
                /*if (canShare()) {

                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://cdn78.picsart.com/186853261001202.gif"))
                            .build();

                    ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            Log.d("gag", result.toString());
                        }

                        @Override
                        public void onCancel() {
                            Log.d("gag", "cancel");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d("gag", error.toString());
                        }
                    });
                }else {
                    Log.d("gag", "foo");
                }*/
                return;
            }

            @Override
            public void onCancel() {
                AlertDialog alert = UserContraller.setupDialogBuilder(context, "Facebook login canceled").create();
                alert.show();
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog alert = UserContraller.setupDialogBuilder(context, error.getMessage()).create();
                alert.show();
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (StringValidation.usernameValidation(context, userName) && StringValidation.passwordValidation(context, password)) {
                    final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(context);
                    animatedProgressDialog.show();
                    final UserContraller userContraller = new UserContraller(context);
                    userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                        @Override
                        public void onRequestReady(int requestNumber, String messege) {
                            if (requestNumber == RequestConstants.LOGIN_USER_SUCCESS_CODE) {
                                UserContraller.writeUserToFile(context, userContraller.getUser());
                                setResult(RESULT_OK);
                                finish();
                            }
                            if (requestNumber == RequestConstants.LOGIN_USER_ERROR_CODE) {
                                AlertDialog alert = UserContraller.setupDialogBuilder(context, ErrorHandler.getErrorMessege(messege)).create();
                                alert.show();
                            }
                            animatedProgressDialog.dismiss();
                        }
                    });
                    userContraller.loginUser(userNameEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(context)) {
                    if (!isLoggedIn()) {
                        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList(FacebookConstants.BASIC_READ_PERMISSIONS));
                    } else {
                        Log.d("gagag", "facebook logout");
                    }
                } else {
                    AlertDialog alert = UserContraller.setupDialogBuilder(context, getString(R.string.no_internet_connection)).create();
                    alert.show();
                }
            }
        });

        findViewById(R.id.open_signup_from_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GifsArtConst.INTENT_OPEN_SIGN_UP, true);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        findViewById(R.id.signin_activity_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResetPasswordActivity.class);
                startActivityForResult(intent, REQUEST_RESET_PASSWORD_ACTIVITY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERSONALIZE_USER_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED, data);
            }
        }
        if (requestCode == REQUEST_RESET_PASSWORD_ACTIVITY) {
            if (resultCode == RESULT_OK) {

            } else {

            }
        }
    }


    private void requestFacebookUserInfo(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("request_facebook_user", object.toString());

                        fbToken = loginResult.getAccessToken().getToken();
                        fbUser = new FacebookUser(object);

                        if (fbUser != null) {
                            final UserContraller userContraller = new UserContraller(context);
                            userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                                @Override
                                public void onRequestReady(int requestNumber, String messege) {
                                    if (requestNumber == RequestConstants.LOGIN_USER_SUCCESS_CODE) {
                                        UserContraller.writeUserToFile(context, userContraller.getUser());
                                        setResult(RESULT_OK);
                                        LoginManager.getInstance().logOut();
                                        finish();
                                    }
                                    if (requestNumber == RequestConstants.LOGIN_USER_ERROR_CODE) {
                                        if (ErrorHandler.userAlreadyExistsError(messege)) {
                                            userContraller.signUpWithFacebook(fbUser);
                                        } else {
                                            setResult(RESULT_CANCELED, ErrorHandler.createErrorMessege(messege));
                                            LoginManager.getInstance().logOut();
                                            finish();
                                        }
                                    }
                                    if (requestNumber == RequestConstants.SIGN_UP_WITH_FACEBOOK_SUCCESS_CODE) {
                                        UserContraller.writeUserToFile(context, userContraller.getUser());
                                        Intent intent = new Intent(context, PersonalizeUserActivity.class);
                                        intent.putExtra("sign_up_with_facebook", true);
                                        startActivityForResult(intent, REQUEST_PERSONALIZE_USER_ACTIVITY);
                                    }
                                    if (requestNumber == RequestConstants.SIGN_UP_WITH_FACEBOOK_ERROR_CODE) {
                                        setResult(RESULT_CANCELED, ErrorHandler.createErrorMessege(messege));
                                        finish();
                                    }
                                }
                            });
                            userContraller.loginUserWithFacebook(fbToken, getSignInParams());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(FIELDS_KEY, REQUEST_FIELDS);
        request.setParameters(parameters);
        GraphRequest.executeBatchAsync(request);
    }

    public static String getSignInParams() {
        JSONObject authJson = new JSONObject();
        try {
            authJson.put("id", fbUser.getId());
            authJson.put("profile_url", fbUser.getLink());
            authJson.put("name", fbUser.getName());
            authJson.put("username", fbUser.getName());
            authJson.put("email", fbUser.getEmail());
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

}
