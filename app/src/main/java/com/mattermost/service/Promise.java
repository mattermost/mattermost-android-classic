/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.service;

import com.mattermost.mattermost.MattermostApplication;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Promise<T> {

    private List<IResultListener<T>> next;
    private T result;
    private String error;
    private JSONObject errorJson;

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
    }

    void onResult(T r, String error) {

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
