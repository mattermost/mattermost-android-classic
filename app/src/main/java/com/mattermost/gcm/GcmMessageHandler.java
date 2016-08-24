/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.mattermost.mattermost.R;
import com.mattermost.mattermost.SplashScreenActivity;
import java.util.LinkedHashMap;

public class GcmMessageHandler extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    public static final String GROUP_KEY_MESSAGES = "group_key_messages";
    private static LinkedHashMap<String,Bundle> channelIdToNotification = new LinkedHashMap<String,Bundle>();

    public static void clearNotifications() {
        channelIdToNotification.clear();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");
        if ("clear".equals(type)) {
            cancelNotification(data.getString("channel_id"));
        } else {
            channelIdToNotification.put(data.getString("channel_id"), data);
            createNotification(true);
        }
        super.onMessageReceived(from, data);
    }

    @Override
    public void onDeletedMessages() { super.onDeletedMessages(); }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        super.onSendError(msgId, error);
    }

    private void createNotification(boolean doAlert) {
        Context context = getBaseContext();

        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS
                | Notification.FLAG_AUTO_CANCEL;

        if (doAlert) {
            defaults = defaults | Notification.DEFAULT_VIBRATE
                    | Notification.DEFAULT_SOUND;
        }

        Intent notificationIntent = new Intent(context, SplashScreenActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIndent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationDismissReceiver.class), 0);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (channelIdToNotification.size() == 0) {
            mNotificationManager.cancel(MESSAGE_NOTIFICATION_ID);
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(GROUP_KEY_MESSAGES)
                .setDefaults(defaults)
                .setContentIntent(contentIndent)
                .setDeleteIntent(deleteIntent)
                .setAutoCancel(true);

        if (channelIdToNotification.size() == 1) {
            Bundle data = channelIdToNotification.entrySet().iterator().next().getValue();
            String body = data.getString("message");
            mBuilder.setContentTitle("Mattermost")
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body));
        } else {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();

            String summaryTitle = String.format("Mattermost (%d)", channelIdToNotification.size());
            mBuilder.setContentTitle(summaryTitle);

            for (Bundle data : channelIdToNotification.values()) {
                style.addLine(data.getString("message"));
            }

            style.setBigContentTitle(summaryTitle);
            mBuilder.setStyle(style);
        }

        mNotificationManager.cancel(MESSAGE_NOTIFICATION_ID);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    private void cancelNotification(String channelId) {
        if (!channelIdToNotification.containsKey(channelId)) {
            return;
    }

        channelIdToNotification.remove(channelId);
        createNotification(false);
    }
}
