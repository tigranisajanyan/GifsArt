package com.gifsart.studio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gifsart.studio.R;
import com.gifsart.studio.social.ErrorHandler;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.StringValidation;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.GifsArtConst;

public class SignUpActivity extends AppCompatActivity {

    private static final int REQUEST_PERSONALIZE_USER_ACTIVITY = 444;

    private SignUpActivity context = this;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        signUpButton = (Button) findViewById(R.id.signin_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringValidation.usernameValidation(context, usernameEditText.getText().toString()) &&
                        StringValidation.emailValidation(context, emailEditText.getText().toString()) &&
                        StringValidation.passwordValidation(context, passwordEditText.getText().toString())) {

                    final UserContraller userContraller = new UserContraller(context);
                    userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                        @Override
                        public void onRequestReady(int requestNumber, String messege) {
                            if (requestNumber == RequestConstants.SIGN_UP_PICSART_SUCCESS_CODE) {
                                user = userContraller.getUser();
                                UserContraller.writeUserToFile(context, user);
                                Intent intent = new Intent(context, PersonalizeUserActivity.class);
                                intent.putExtra(GifsArtConst.INTENT_SIGN_UP_WITH_FACEBOOK, false);
                                startActivityForResult(intent, REQUEST_PERSONALIZE_USER_ACTIVITY);
                            }
                            if (requestNumber == RequestConstants.SIGN_UP_PICSART_ERROR_CODE) {
                                AlertDialog alert = UserContraller.setupDialogBuilder(SignUpActivity.this, ErrorHandler.getErrorMessege(messege)).create();
                                alert.show();
                            }
                        }
                    });
                    userContraller.signUpToPicsArt(usernameEditText.getText().toString(), passwordEditText.getText().toString(), emailEditText.getText().toString());
                }
            }
        });

        findViewById(R.id.signup_activity_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.open_signin_from_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GifsArtConst.INTENT_OPEN_SIGN_IN, true);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERSONALIZE_USER_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED, data);
            }
            finish();
        }
    }

}
