package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gifsart.studio.R;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.GifsArtConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    private String userName;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        //emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        signUpButton = (Button) findViewById(R.id.signup_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEnteredUserName(usernameEditText.getText().toString()) || checkEnteredEmail(emailEditText.getText().toString()) || checkEnteredPassword(passwordEditText.getText().toString())) {
                    final UserContraller userContraller = new UserContraller(SignUpActivity.this);
                    userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                        @Override
                        public void onRequestReady(int requestNumber, String messege) {
                            if (requestNumber == RequestConstants.SIGN_UP_PICSART_SUCCESS_CODE) {
                                editor = sharedPreferences.edit();
                                editor.putString("user_api_key", userContraller.getUserApiKey());
                                editor.commit();
                                Intent intent = new Intent();
                                intent.putExtra("name", userContraller.getUserName());
                                intent.putExtra("photo_url", userContraller.getUserPhotoUrl());
                                setResult(RESULT_OK, intent);
                                Log.d("gaga", messege);
                            }
                            if (requestNumber == RequestConstants.SIGN_UP_PICSART_ERROR_CODE) {
                                setResult(RESULT_CANCELED);
                            }
                            finish();
                        }
                    });
                    userContraller.signUpToPicsArt(usernameEditText.getText().toString(), passwordEditText.getText().toString(), emailEditText.getText().toString());
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean checkEnteredUserName(String userName) {
        if (userName.equals("") || userName.length() < 6) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEnteredEmail(String email) {
        if (email.equals("") || email.length() < 6) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEnteredPassword(String password) {
        if (password.equals("") || password.length() < 6) {
            return false;
        } else {
            return true;
        }
    }

}
