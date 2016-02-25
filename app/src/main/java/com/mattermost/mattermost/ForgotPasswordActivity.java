/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mattermost.model.User;
import com.mattermost.service.IResultListener;
import com.mattermost.service.MattermostService;
import com.mattermost.service.Promise;

public class ForgotPasswordActivity extends AppChildActivity {

    private TextView errorMessage;
    private EditText emailAddress;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        errorMessage = (TextView) findViewById(R.id.error_message);
        emailAddress = (EditText) findViewById(R.id.email_address);
        proceed = (Button) findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doForgotPassword();
            }
        });
    }

    private void doForgotPassword() {
        MattermostService.service
                .forgotPassword(emailAddress.getText().toString())
                .then(new IResultListener<User>() {
                    @Override
                    public void onResult(Promise<User> promise) {
                        if (promise.getError() != null) {
                            errorMessage.setText(promise.getError());
                        } else {
                            errorMessage.setText("");
                            AppActivity.alert("Email sent with link to reset password", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                        }
                    }
                });
    }
}
