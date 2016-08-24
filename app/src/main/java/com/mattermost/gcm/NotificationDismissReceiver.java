/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GcmMessageHandler.clearNotifications();
    }
}