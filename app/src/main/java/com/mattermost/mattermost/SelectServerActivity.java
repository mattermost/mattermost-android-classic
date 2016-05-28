/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mattermost.model.InitialLoad;
import com.mattermost.service.IResultListener;
import com.mattermost.service.MattermostService;
import com.mattermost.service.Promise;

public class SelectServerActivity extends AppActivity {

    public static final int START_CODE = 11;

    EditText serverName;
    Button proceed;
    String server;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_server);

        serverName = (EditText) findViewById(R.id.server_name);
        serverName.setSelection(serverName.getText().length());

        proceed = (Button) findViewById(R.id.proceed);
        errorMessage = (TextView) findViewById(R.id.error_message);

        String baseUrl = MattermostService.service.getBaseUrl();

        if (baseUrl != null && baseUrl.length() > 0) {
            serverName.setText(baseUrl);
        }

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelectServer();
            }
        });

        serverName.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    proceed.performClick();
                }
                return false;
            }
        });
    }

    private void doSelectServer() {
        server = serverName.getText().toString();
        if (server.isEmpty()) {
            errorMessage.setText(R.string.error_server_url_empty);
            return;
        } else {
            errorMessage.setText("");
        }

        if (!server.contains("http")) {
            server = "http://" + server;
        }
        try {
            MattermostService.service.init(server);
        } catch (Exception e) {
            errorMessage.setText(R.string.error_mattermost_server);
            MattermostService.service.removeBaseUrl();
            Log.e("Error", e.toString());
        }

        MattermostService.service.initialLoad()
                .then(new IResultListener<InitialLoad>() {
                    @Override
                    public void onResult(Promise<InitialLoad> promise) {
                        if (promise.getError() != null) {
                            MattermostService.service.removeBaseUrl();
                            errorMessage.setText(R.string.error_mattermost_server);
                            Log.e("Error", promise.getError());
                        } else {
                            errorMessage.setText("");
                            MattermostService.service.init(server);
                            Intent intent = new Intent(SelectServerActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
