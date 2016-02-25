/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mattermost.model.User;
import com.mattermost.service.IResultListener;
import com.mattermost.service.MattermostService;
import com.mattermost.service.Promise;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.List;

public class SelectTeamActivity extends AppActivity {

    public static final int START_CODE = 11;

    EditText teamName;
    Button proceed;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_team);

        teamName = (EditText) findViewById(R.id.team_name);
        proceed = (Button) findViewById(R.id.proceed);
        errorMessage = (TextView) findViewById(R.id.error_message);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelectTeam();
            }
        });
    }

    private void doSelectTeam() {
        String team = teamName.getText().toString();
        if (team.isEmpty()) {
            errorMessage.setText(R.string.error_team_url_empty);
            return;
        } else {
            errorMessage.setText("");
        }

        try {
            MattermostService.service.init(team);
        } catch (Exception e) {
            errorMessage.setText(R.string.error_mattermost_server);
            MattermostService.service.removeBaseUrl();
            Log.e("Error", e.toString());
            return;
        }

        MattermostService.service.findTeamByName(MattermostService.service.getTeam())
                .then(new IResultListener<Boolean>() {
                    @Override
                    public void onResult(Promise<Boolean> promise) {
                        if (promise.getError() != null) {
                            MattermostService.service.removeBaseUrl();
                            errorMessage.setText(R.string.error_mattermost_server);
                            Log.e("Error", promise.getError());
                        } else {
                            if (!promise.getResult()) {
                                MattermostService.service.removeBaseUrl();
                                errorMessage.setText(R.string.error_team_url);
                                Log.e("Error", "couldn't find team");
                            } else {
                                errorMessage.setText("");
                                MattermostService.service.init(teamName.getText().toString());
                                Intent intent = new Intent(SelectTeamActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
    }
}
