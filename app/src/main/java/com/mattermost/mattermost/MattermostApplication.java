/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
//import android.webkit.CookieSyncManager;

import com.mattermost.service.MattermostService;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MattermostApplication extends Application {

    public static MattermostApplication current;
    public static Handler handler;

    public MattermostApplication() {
        super();
        current = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
  //      CookieSyncManager.createInstance(this);
        MattermostService.service = new MattermostService(this);

        handler = new Handler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });

    }

    public static String toString(Throwable ex) {
        String msg = ex.toString();
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);

            ex.printStackTrace(pw);

            pw.flush();

            msg = sw.toString();
        } catch (Exception e) {
        } finally {
            try {
                if (pw != null)
                    pw.close();
                if (sw != null)
                    sw.close();
            } catch (Exception doNothing) {
            }
        }

        return msg;
    }

    public static void logError(Exception ex) {
        Log.e("Error", toString(ex));
    }
}
