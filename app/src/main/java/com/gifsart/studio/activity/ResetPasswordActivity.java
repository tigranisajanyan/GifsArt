package com.gifsart.studio.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gifsart.studio.R;
import com.gifsart.studio.social.ErrorHandler;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.StringValidation;
import com.gifsart.studio.social.UserContraller;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText resetPasswordEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordEditText = (EditText) findViewById(R.id.reset_password_edit_text);
        resetPasswordButton = (Button) findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringValidation.emailValidation(ResetPasswordActivity.this, resetPasswordEditText.getText().toString())) {
                    UserContraller userContraller = new UserContraller(ResetPasswordActivity.this);
                    userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                        @Override
                        public void onRequestReady(int requestNumber, String messege) {
                            if (requestNumber == RequestConstants.REQUEST_RESET_PASSWORD_SUCCESS_CODE) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                AlertDialog alert = UserContraller.setupDialogBuilder(ResetPasswordActivity.this, ErrorHandler.getErrorMessege(messege)).create();
                                alert.show();
                            }
                        }
                    });
                    userContraller.resetUserPassword(resetPasswordEditText.getText().toString());
                }
            }
        });

        findViewById(R.id.reset_password_activity_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


}
