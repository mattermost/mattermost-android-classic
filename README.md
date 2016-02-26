# Mattermost Android Application 

Android application for use with Mattermost server 2.1 and higher (http://www.mattermost.org/download/) 

> Note: The Mattermost Android App requires APIs being introduced in Mattermsot 2.1, which releases on March 16, 2016. You can test Android app functionality by joining the nightly builds server at https://pre-release.mattermost.com/core or by compiling [Mattermost platform](https://github.com/mattermost/platform) from the latest source code. 

#### Supported Platforms 

- Smartphones and tablets running Android 4.4.2+ 

#### Requirements for Deployment 

1. Understanding of [Mattermost push notifications](http://docs.mattermost.com/administration/config-settings.html#push-notification-settings). 
2. Experience compiling and deploying iOS applications either to Google Play or an Enterprise App Store 
3. An Google Developer account and appropriate Google devices to compile, test and deploy the application

#### Installation 

1. Install [Mattermost 2.1 or higher](http://www.mattermost.org/download/).
2. Compile and deploy this Android application to your Enterprise AppStore or publicly.
3. Install [the latest stable release of the Mattermost Push Notifications Server](https://github.com/mattermost/push-proxy) using the private and public keys generated for your Android application from step 2.
4. In the Mattermost Platform Server go to **System Console** > **Email Settings** > **Push Notifications Server** and add the web address of the Mattermost Push Notifications Server. Set **System Console** > **Send Push Notifications** to `true`.
5. On your Android device, download and install your app and enter the **Team URL** and credentials based on a team set up on your Mattermost Platform Server


