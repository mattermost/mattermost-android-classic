/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.support.v7.app.ActionBar;

public class AppChildActivity extends AppActivity {

    @Override
    protected void onResume() {
        super.onResume();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
