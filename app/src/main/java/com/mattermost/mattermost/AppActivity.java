/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mattermost.service.MattermostService;

public class AppActivity extends AppCompatActivity {

    public static AppActivity current;
    protected MattermostService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = this;
        this.service = MattermostService.service;
    }

    public static void alert(String s, final DialogInterface.OnClickListener listener) {
        AlertDialog dialog = (new AlertDialog.Builder(current))
                .setTitle("Mattermost Classic")
                .setMessage("Email sent with reset link")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onClick(dialog, which);
                        }
                    }
                })
                .create();

        dialog.show();
    }
}
