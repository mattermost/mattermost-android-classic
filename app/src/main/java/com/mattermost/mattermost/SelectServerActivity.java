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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

//import com.mattermost.model.InitialLoad;
import com.mattermost.model.Ping;
import com.mattermost.service.IResultListener;
import com.mattermost.service.MattermostService;
import com.mattermost.service.Promise;

public class SelectServerActivity extends AppActivity {

    public static final int START_CODE = 11;

    String server;
    Spinner serverPrefix;
    EditText serverName;
    Button proceed;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_server);

        serverPrefix = (Spinner) findViewById(R.id.server_prefix);
        serverName = (EditText) findViewById(R.id.server_name);
        proceed = (Button) findViewById(R.id.proceed);
        errorMessage = (TextView) findViewById(R.id.error_message);

        String baseUrl = MattermostService.service.getBaseUrl();

        if (baseUrl != null && baseUrl.length() > 0) {
            String prefix = baseUrl.substring(0, baseUrl.lastIndexOf("://") + 3);
            baseUrl = baseUrl.substring(prefix.length());
            serverName.setText(baseUrl);

            if (prefix != null && prefix.length() > 0) {
                if (prefix.equals("https://")) {
                    serverPrefix.setSelection(0);
                } else {
                    serverPrefix.setSelection(1);
                }
            }
        }

        serverName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    proceed.performClick();
                }
                return false;
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelectServer();
            }
        });
    }

    private void doSelectServer() {
        server = serverPrefix.getSelectedItem().toString() + serverName.getText().toString();
        if (server.isEmpty()) {
            errorMessage.setText(R.string.error_server_url_empty);
            return;
        } else {
            errorMessage.setText("");
        }

        try {
            MattermostService.service.init(server);
        } catch (Exception e) {
            errorMessage.setText(R.string.error_mattermost_server);
            MattermostService.service.removeBaseUrl();
            Log.e("Error", e.toString());
            return;
        }

        MattermostService.service.pingV4()
                .then(new IResultListener<Ping>() {
                    @Override
                    public void onResult(Promise<Ping> promise) {
                        if (promise.getError() != null) {
                            MattermostService.service.pingV3()
                                    .then(new IResultListener<Ping>() {
                                        @Override
                                        public void onResult(Promise<Ping> promise) {
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
                        } else {
                            errorMessage.setText("");
                            MattermostService.service.SetV4();
                            MattermostService.service.init(server);
                            Intent intent = new Intent(SelectServerActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
