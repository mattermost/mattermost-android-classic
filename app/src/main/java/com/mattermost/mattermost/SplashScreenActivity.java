/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mattermost.gcm.RegistrationIntentService;

import java.io.File;

public class SplashScreenActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MattermostApplication.handler.post(new Runnable() {
            @Override
            public void run() {
                onAfterCreate();
            }
        });
    }

    private void onAfterCreate() {
        enableHttpResponseCache();

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        boolean teamSet = service.getBaseUrl() != null;
        intent = new Intent(this, teamSet ? MainActivity.class : SelectServerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 20 * 1024 * 1024; // 20 MiB
            File httpCacheDir = new File(getExternalCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
            Log.i("HTTP", httpCacheDir.getAbsolutePath());
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.e("HTTP", "HTTP response cache is unavailable.");
        }
    }
}
