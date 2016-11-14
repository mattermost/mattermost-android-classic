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
//        int i = url.lastIndexOf("/");
//        if (i != -1) {
//            String team = url.substring(i + 1);
//            setTeam(team);
//        }

        apiClient = retrofit.create(MattermostAPI.class);
    }

//    public Promise<User> login(String email, String password) {
//        User user = new User();
//        user.name = getTeam();
//        user.email = email;
//        user.password = password;
//
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String deviceId = sharedPreferences.getString("device_id", null);
//        user.deviceId = "android:" + deviceId.toString();
//
//        return apiClient.login(user);
//    }

    public Promise<User> attachDevice() {
        User user = new User();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = sharedPreferences.getString("device_id", null);

        if (deviceId == null) {
            return null;
        }

        user.deviceId = "android:" + deviceId.toString();

        return apiClient.attachDevice(user);
    }

    public Promise<InitialLoad> initialLoad() {
        return apiClient.initialLoad();
    }

//    public Promise<Boolean> findTeamByName(String name) {
//        User user = new User();
//        user.name = name;
//        return apiClient.findTeamByName(user);
//    }
//
//    public Promise<User> signup(String email, String name) {
//        User user = new User();
//        user.email = email;
//        user.name = name;
//        return apiClient.signup(user);
//    }
//
//
//
//    public Promise<User> forgotPassword(String emailAddress) {
//        User user = new User();
//        user.email = emailAddress;
//        return apiClient.sendPaswordReset(user);
//    }

//    public String getTeam() {
//        if (team == null) {
//            team = preferences.getString("Team", "");
//        }
//
//        return team;
//    }
//
//    public void setTeam(String name) {
//        if (name == null) {
//            preferences.edit().remove("Team").commit();
//        } else {
//            preferences.edit().putString("Team", name).commit();
//        }
//
//        team = name;
//    }

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
        cookieStore.clear();
    }

    public interface MattermostAPI {

        @Headers("X-Requested-With: XMLHttpRequest")
        @POST("/api/v3/users/attach_device")
        Promise<User> attachDevice(@Body User user);

        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("/api/v3/users/initial_load")
        Promise<InitialLoad> initialLoad();

//        @POST("/api/v1/users/login")
//        Promise<User> login(@Body User user);
//
//        @POST("/api/v1/users/send_password_reset")
//        Promise<User> sendPaswordReset(@Body User user);
//
//        @POST("/api/v1/teams/find_team_by_name")
//        Promise<Boolean> findTeamByName(@Body User user);
//
//        @POST("/api/v1/teams/email_teams")
//        Promise<List<User>> findTeams(@Body User user);
//
//        @POST("/api/v1/teams/signup")
//        Promise<User> signup(@Body User user);
    }
}
