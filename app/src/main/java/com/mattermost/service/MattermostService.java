/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mattermost.mattermost.R;
import com.mattermost.model.User;
import com.mattermost.model.Ping;
import com.mattermost.model.InitialLoad;
import com.mattermost.service.jacksonconverter.JacksonConverterFactory;
import com.mattermost.service.jacksonconverter.PromiseConverterFactory;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.ResponseBody;

import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.List;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.Retrofit;
import retrofit.http.Headers;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.GET;
import retrofit.http.PUT;



public class MattermostService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static MattermostService service;
    private final WebkitCookieManagerProxy cookieStore;

    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    private Retrofit retrofit;
    private MattermostAPI apiClient;
    private SharedPreferences preferences;
    private String baseUrl;
    private String team = null;

    public MattermostService(Context context) {
        this.context = context;
        String userAgent = context.getResources().getString(R.string.app_user_agent);

        cookieStore = new WebkitCookieManagerProxy();

        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
        client.setCookieHandler(cookieStore);
        preferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean("loggedIn", false);
    }

    public String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = preferences.getString("baseUrl", null);
        }
        return baseUrl;
    }

    public void removeBaseUrl() {
        preferences.edit().remove("baseUrl").commit();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void init(String baseUrl) {
        this.baseUrl = baseUrl;
        preferences.edit().putString("baseUrl", baseUrl).commit();

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl);
        builder.client(client);
        builder.addConverterFactory(JacksonConverterFactory.create());
        builder.addCallAdapterFactory(PromiseConverterFactory.create());

        retrofit = builder.build();

        String url = baseUrl;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        apiClient = retrofit.create(MattermostAPI.class);
    }

    public Promise<User> attachDevice() {
        User user = new User();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = sharedPreferences.getString("device_id", null);

        if (deviceId == null) {
            return null;
        }

        user.deviceId = "android:" + deviceId.toString();

        if (this.isV4()) {
            return apiClient.attachDeviceV4(user);
        } else{
            return apiClient.attachDeviceV3(user);
        }
    }


    public Promise<Ping> pingV4() {
        return apiClient.pingV4();
    }

    public Promise<Ping> pingV3() {
        return apiClient.pingV3();
    }

    public boolean isV4() {
        return "true".equals(preferences.getString("V4", "false"));
    }

    public void SetV4() {
        preferences.edit().putString("V4", "true").commit();
    }

    public boolean isAttached() {
        return "true".equals(preferences.getString("AttachedId", "false"));
    }

    public void SetAttached() {
        preferences.edit().putString("AttachedId", "true").commit();
    }

    public void SetAttached(boolean attached) {
        preferences.edit().putString("AttachedId", "" + attached).commit();
    }

    public String GetLastPath() {
        return preferences.getString("LastPath", "");
    }

    public void SetLastPath(String lastPath) {
        preferences.edit().putString("LastPath", lastPath).commit();
    }

    public void logout() {
        preferences.edit().remove("AttachedId").commit();
        preferences.edit().remove("Team").commit();
        preferences.edit().remove("baseUrl").commit();
        preferences.edit().remove("loggedIn").commit();
        preferences.edit().remove("LastPath").commit();
        preferences.edit().remove("V4").commit();
        cookieStore.clear();
    }

    public interface MattermostAPI {

        @Headers("X-Requested-With: XMLHttpRequest")
        @POST("/api/v3/users/attach_device")
        Promise<User> attachDeviceV3(@Body User user);

        @Headers("X-Requested-With: XMLHttpRequest")
        @PUT("/api/v4/users/sessions/device")
        Promise<User> attachDeviceV4(@Body User user);

        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("/api/v4/system/ping")
        Promise<Ping> pingV4();

        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("/api/v3/general/ping")
        Promise<Ping> pingV3();
    }
}
