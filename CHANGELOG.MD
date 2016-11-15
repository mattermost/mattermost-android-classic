# Mattermost Android Application Changelog

## Release v3.5.0  

Release Date 2016-11-16

### Bug Fixes
- Fixed push notifications sometimes not being delivered.
- Removed the red loading circle in the center of the screen.
- Changed `https://` sizing on the server page so it is not cut off on smaller screens.
- Fixed an issue where the "download" link was not working for file attachments.
- Screen rotation is now disabled to avoid styling errors.

### Compatibility

- Android Native App does not support HTTP2.

### Contributors

Many thanks to all our contributors. In alphabetical order:

- [coreyhulen](https://github.com/coreyhulen)
- [DavidLu1997](https://github.com/DavidLu1997)
- [dmeza](https://github.com/dmeza)
- [it33](https://github.com/it33)

## Release v3.4.0  

Release Date 2016-09-16

### Improvements
- Push notifications now stack when multiple notifications are received, and clear when the message is read elsewhere.

### Bug Fixes
- Added more exception URLs to fix issues with Google and SAML sign in.

### Contributors

Many thanks to all our contributors. In alphabetical order:

- [coreyhulen](https://github.com/coreyhulen)
- [enahum](https://github.com/enahum)
- [jwilander](https://github.com/jwilander)

## Release v3.3.0  

Released 2016-08-23

### Improvements
- Enter key now creates a new line instead of sending the message.
- Made it easier for users to discover the mobile app by adding a download page that appears when the site is accessed on a mobile web browser. Also added download links to the welcome email, tutorial, and main menu.
- Permalinks are now available on mobile.
- Clicking on the ... menu when in the right-hand sidebar view is now easier.
- Auto-complete enabled for "from:" and "in:".

### Bug Fixes
- Fixed an issue with Google and Office 365 SSO not working

### Contributors

Many thanks to all our contributors. In alphabetical order:

- [coreyhulen](https://github.com/coreyhulen)
- [macdabby](https://github.com/macdabby)
 
## Release v3.2.0  

Released 2016-07-16

### Improvements
- Changed colors on login page to blue
- Added a dropdown selector on the login page for https:// or http://
- Changed the server selection field on the login page to have a URL type, to take advantage of the Android keyboard 

### Bug Fixes
- When a session ends or is revoked, push notifications should now start sending again when the user logs back in
- Fixed an issue with icon rendering poorly on some devices

### Contributors

Many thanks to all our contributors. In alphabetical order:

- [coreyhulen](https://github.com/coreyhulen)
- [DavidLu1997](https://github.com/DavidLu1997)
- [whites11](https://github.com/whites11)

## Release v3.1.0  

Released 2016-06-16

### Features 
- **Account Settings** > **Notifications** > **Mobile push notifications** option lets users to enable mobile push notifications for all activity, mentions only, or to disable push notifications. 
- Push notifications sent even if a user is online on desktop.
- Removed auto-capitalization on login screen, so email is no longer capitalized.

### Contributors

Many thanks to all our contributors. In alphabetical order:

- [coreyhulen](https://github.com/coreyhulen)
- [DavidLu1997](https://github.com/DavidLu1997)
- [it33](https://github.com/it33)
- [lfbrock](https://github.com/lfbrock)
- [nineinchnick](https://github.com/nineinchnick)

## Release v3.0.0  

Released 2016-05-16

### Features   
- Added support for multiple teams on the same server
- Added auto-correct
- New option to include message snippets in push notifications

### Breaking Change  
- Users of Mattermost 3.0 server need to install new Android 3.0 app. Android 2.x apps are not compatible with Mattermost 3.0 server, and Android 3.0 app is not compatible with Mattermost 2.x server. [See Mattermost v3.0 changelog for further details](http://docs.mattermost.com/administration/changelog.html#release-v3-0-3)

### Contributors

Many thanks to all our contributors. In alphabetical order:

- [arusahni](https://github.com/arusahni)
- [coreyhulen](https://github.com/coreyhulen)
- [JohnMaguire](https://github.com/JohnMaguire)
- [lindy65](https://github.com/lindy65)
- [nicolas-raoul](https://github.com/nicolas-raoul)
- [ptersilie](https://github.com/ptersilie)

## Release v2.1.0

Released 2016-03-16

### Release Highlights

- First official release of Mattermost Android app, a team communication service for sharing messages and files across PCs and phones, with archiving and instant search 
- Supports push notifications for devices running Android 4.4.2+. Requires Mattermost server v2.1 and higher. See [list of tested devices](https://github.com/mattermost/android/blob/master/DEVICES.md)
 
### Features

Push Notifications
- Ability to enable push notifications for mobile devices

Messaging and File Sharing

- Send messages, comments, files and images across public, private and 1-1 channels
- Personalize notifications for unreads and mentions by channel
- Use #hashtags to tag and find messages, discussions and files

Archiving and Search 
 
- Search public and private channels for historical messages and comments 
- View recent mentions of your name, username, nickname, and custom search terms

Anywhere Access

- Use Mattermost from a native Andoid app as well as from web-enabled phones and PCs
- Define team-specific branding and color themes across your devices
