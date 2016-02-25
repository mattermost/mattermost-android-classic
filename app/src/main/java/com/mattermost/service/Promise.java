/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.service;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.mattermost.images.AnimatedCircleDrawable;
import com.mattermost.mattermost.AppActivity;
import com.mattermost.mattermost.MattermostApplication;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Promise<T> {

    private List<IResultListener<T>> next;
    private T result;
    private String error;
    private JSONObject errorJson;

    PopupWindow busy = null;

    public static PopupWindow show() {
        Activity currentActivity = AppActivity.current;
        PopupWindow pw = new PopupWindow(currentActivity);
        pw.setFocusable(false);
        pw.setBackgroundDrawable(new ColorDrawable(0));
        ImageView img = new ImageView(currentActivity);
        pw.setContentView(img);
        View view = currentActivity.getWindow().getDecorView();
        pw.setWidth(60);
        pw.setHeight(60);
        AnimatedCircleDrawable a = new AnimatedCircleDrawable(Color.RED, 5, Color.TRANSPARENT, 30);
        img.setImageDrawable(a);
        pw.showAtLocation(view, Gravity.LEFT | Gravity.TOP, view.getWidth() / 2 - 30, view.getHeight() / 2 - 30);
        a.start();

        return pw;
    }

    public Promise() {
        next = new ArrayList<IResultListener<T>>();
    }

    public T getResult() {
        return result;
    }

    public Callback<T> callback() {
        return new Callback<T>() {
            public void onResponse(final Response<T> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    onResult(response.body(), null);
                } else {
                    try {
                        onResult(null, response.errorBody().string());
                    } catch (Exception ex) {
                        onResult(null, MattermostApplication.toString(ex));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onResult(null, MattermostApplication.toString(t));
            }
        };
    }

    public JSONObject getErrorJson() {
        return errorJson;
    }

    public String getError() {
        return error;
    }

    public Promise<T> then(IResultListener<T> action) {
        next.add(action);
        return this;
    }

    public void onStarted() {
        MattermostApplication.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    busy = show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void onResult(T r, String error) {
        MattermostApplication.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (busy != null) {
                        busy.dismiss();
                    }
                    busy = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        result = r;

        if (error != null) {
            // lets parse...
            error = error.trim();
            if (error.startsWith("{") && error.endsWith("}")) {
                try {
                    JSONTokener tokener = new JSONTokener(error);
                    errorJson = (JSONObject) tokener.nextValue();
                    if (errorJson.has("message")) {
                        this.error = errorJson.optString("message");
                    }
                } catch (Exception ex) {
                    // ignore...
                    this.error = error;
                }
            } else {
                this.error = error;
            }
        }

        MattermostApplication.handler.post(new Runnable() {
            @Override
            public void run() {
                for (IResultListener<T> resultListener : next) {
                    try {
                        resultListener.onResult(Promise.this);
                    } catch (Exception ex) {
                        MattermostApplication.logError(ex);
                    }
                }
            }
        });

    }

    public static Promise<String> whenAll(Promise... promises) {
        final Promise<String> promise = new Promise<String>();
        final StringBuilder sb = new StringBuilder();
        final List<Object> pending = new ArrayList<Object>(promises.length);
        for (Promise p : promises) {
            pending.add(p);
            p.then(new IResultListener() {
                @Override
                public void onResult(Promise result) {
                    if (result.getError() != null) {
                        sb.append(result.getError());
                    }
                    pending.remove(result);
                    if (pending.isEmpty()) {
                        String errors = sb.toString().trim();
                        if (errors.length() > 0) {
                            promise.onResult(null, errors);
                        } else {
                            promise.onResult(null, null);
                        }
                    }
                }
            });
        }
        return promise;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;
}
