# Mattermost Android Application 

Mattermost Android Application for use with Mattermost server 3.0 and higher (http://www.mattermost.org/download/).

You can download a compiled version of the latest release from [Google Play.](https://about.mattermost.com/downloads/) 

**Note:**
We are working on [new mobile apps](https://github.com/mattermost/mattermost-mobile) using React Native to replace this application. This repo is in maintenance mode, so only bug fixes will be accepted.

---

#### Beta Testing

- Please see [Mattermost Android App testing documentation](https://github.com/mattermost/android/blob/master/TESTING.md) for how to help test new releases.

#### Supported Platforms 

- See [listing of verified Android devices](DEVICES.md) on which this application has been manually tested.
- Minimum required Android operating system is 4.4.2+ with Google Play Services enabled.

#### Requirements for Deployment 

1. Understanding of [Mattermost push notifications](http://docs.mattermost.com/administration/config-settings.html#push-notification-settings). 
2. Experience compiling and deploying Android applications to Google Play or as .apk files within companies.
3. An Google Developer account and appropriate Google devices to compile, test and deploy the application.

#### Installation 

1. Install [Mattermost 3.0 or higher](http://www.mattermost.org/download/).
2. Update app/src/main/res/values/strings.xml and change the value of gcm_sender_id to your app id available [here](https://console.cloud.google.com).
3. Compile and deploy this Android application with your own private key to an .apk file distributed to your team. Please DO NOT deploy this app in the Google Play store.
4. Install [the latest stable release of the Mattermost Push Notifications Server](https://github.com/mattermost/push-proxy) using the private and public keys generated for your Android application from step 2.
5. In the Mattermost Platform Server go to **System Console** > **Email Settings** > **Push Notifications Server** and add the web address of the Mattermost Push Notifications Server. Set **System Console** > **Send Push Notifications** to `true`.
6. On your Android device, download and install your app and enter the **Team URL** and credentials based on a team set up on your Mattermost Platform Server.

#### Bugs, Feature Ideas and Troubleshooting 

- Please see [documentation on filing Bugs, adding Feature Ideas and getting help Troubleshooting](http://docs.mattermost.com/process/community-systems.html).

#### Known Issues

- Back button does not work on Channel Navigation or User Settings views
