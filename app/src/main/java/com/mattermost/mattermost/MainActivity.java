/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mattermost.model.User;
import com.mattermost.service.IResultListener;
import com.mattermost.service.MattermostService;
import com.mattermost.service.Promise;


import java.net.HttpCookie;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends WebViewActivity {

    WebView webView;
    Uri appUri;

    String senderID;
    GoogleCloudMessaging gcm;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        appUri = Uri.parse(service.getBaseUrl());

        webView = (WebView) findViewById(R.id.web_view);

        initProgressBar(R.id.webViewProgress);
        initWebView(webView);

        String url = service.getBaseUrl();
        if (!url.endsWith("/"))
            url += "/";
        url += "channels/town-square";
        webView.loadUrl(url);

        dialog = new ProgressDialog(this);
        dialog.setMessage(this.getText(R.string.loading));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void setWebViewClient(WebView view) {
        view.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.hide();
                Log.i("MainActivity", "onPageFinished while loading");
                Log.i("onPageFinished", url);

                // Check to see if we need to attach the device Id
                if (url.toLowerCase().endsWith("/channels/town-square")) {
                    if (isLoggedIn() && !MattermostService.service.isAttached()) {
                        Log.i("MainActivity", "Attempting to attach device id");
                        MattermostService.service.init(MattermostService.service.getBaseUrl());
                        MattermostService.service.attachDevice()
                                .then(new IResultListener<User>() {
                                    @Override
                                    public void onResult(Promise<User> promise) {
                                        if (promise.getError() != null) {
                                            Log.e("MainActivity", promise.getError());
                                        } else {
                                            Log.i("MainActivity", "Attached device_id to session");
                                            MattermostService.service.SetAttached();
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.i("MainActivity", "onReceivedError while loading");
                Log.i("Error", error.toString());
            }

            @Override
            public void onReceivedHttpError (WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.i("MainActivity", "onReceivedHttpError while loading");
                Log.i("Error", errorResponse.toString());
            }

            @Override
            public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
                Log.i("MainActivity", "onReceivedError while loading (d)");
                Log.i("Error", description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);

                if (!isLoggedIn()) {
                    return false;
                }

                if (!uri.getHost().equalsIgnoreCase(appUri.getHost())) {
                    openUrl(uri);
                    return true;
                }

                if (uri.getPath().startsWith("/static/help")) {
                    openUrl(uri);
                    return true;
                }

                if (uri.getPath().startsWith("/api/v1/files/get/")) {
                    openUrl(uri);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // Check to see if the user was trying to logout
                if (url.toLowerCase().endsWith("/logout")) {
                    MattermostApplication.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLogout();
                        }
                    });
                }

                String baseUrl = "";
                int i = service.getBaseUrl().lastIndexOf("/");
                if (i != -1) {
                    baseUrl = service.getBaseUrl().substring(0, i);

                }

                // If you're at the root then logout and so the select team view
                if (url.toLowerCase().endsWith(baseUrl + "/") || url.toLowerCase().endsWith(baseUrl)) {
                    MattermostApplication.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLogout();
                        }
                    });
                }

                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    private void openUrl(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private boolean isLoggedIn() {
        String baseUrl = service.getBaseUrl();
        if (baseUrl == null) {
            return false;
        }

        String cookies = CookieManager.getInstance().getCookie(baseUrl);
        if (cookies == null)
            return false;
        if (cookies.trim().isEmpty())
            return false;
        if (!cookies.contains("MMTOKEN"))
            return false;
        return true;
    }

    @Override
    protected void onLogout() {
        Log.i("MainActivity", "onLogout called");
        super.onLogout();

        MattermostService.service.logout();

        Intent intent = new Intent(this, SelectTeamActivity.class);
        startActivityForResult(intent, SelectTeamActivity.START_CODE);
        finish();
    }
}
