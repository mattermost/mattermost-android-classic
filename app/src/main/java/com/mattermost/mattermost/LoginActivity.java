/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mattermost.model.User;
import com.mattermost.service.IResultListener;
import com.mattermost.service.MattermostService;
import com.mattermost.service.Promise;

public class LoginActivity extends AppChildActivity {

    public static final int START_CODE = 12;
    EditText emailAddress;
    EditText password;
    Button proceed;
    Button forgotPassword;
    TextView errorMessage;
    TextView loginSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailAddress = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);
        proceed = (Button) findViewById(R.id.proceed);
        forgotPassword = (Button) findViewById(R.id.forgot_password);
        errorMessage = (TextView) findViewById(R.id.error_message);

        loginSubTitle = (TextView) findViewById(R.id.login_sub_title);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelectServerActivity.class);
        startActivity(intent);
        finish();
    }

}
