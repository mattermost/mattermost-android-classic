/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.mattermost;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Build;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WebViewActivity extends AppActivity {

    ProgressBar progressBar;
    FrameLayout progressBarFrame;
    UploadHandler mUploadHandler;

    protected WebView webView;
    private boolean isProgressVisible = false;

    protected void initProgressBar(int id) {
        this.progressBar = (ProgressBar) this.findViewById(id);
        this.progressBar.setMax(100);
        this.progressBar.setIndeterminate(false);
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    protected void initWebView(WebView view) {
        webView = view;
        setProgressBarVisibility(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString(settings.getUserAgentString() + " Web-Atoms-Mobile-WebView");
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        view.setDownloadListener(getDownloadListener());

        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);

        setWebViewClient(view);
        setWebChromeClient(view);
    }

    protected void initWebView(int webViewID, final int startPageID, final String url) {
        try {
            webView = (WebView) this.findViewById(webViewID);

            initWebView(webView);

            if (startPageID > 0) {
                MattermostApplication.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String content = getRawString(startPageID);
                            webView.loadDataWithBaseURL(null, content, "text/html", "base64", null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        MattermostApplication.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadUrl(url);
                            }
                        });
                    }
                });
            } else {
                webView.loadUrl(url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private DownloadListener getDownloadListener() {
        return new DownloadListener() {
            public void onDownloadStart(
                String url,
                String userAgent,
                String contentDisposition,
                String mimetype,
                long contentLength
            ) {
                Uri uri = Uri.parse(url);
                Request request = new Request(uri);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle("File download from Mattermost");

                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie != null) {
                    request.addRequestHeader("cookie", cookie);
                }

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
           }
        };
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        setProgressBarVisibility(true);
        super.onResume();
    }

    protected void setWebViewClient(WebView view) {
    }

    protected void onLogout() {
    }

    protected void setWebChromeClient(WebView view) {
        view.setWebChromeClient(new MyWebChromeClient());
    }

    private void reportProgress(final int newProgress) {
        if (newProgress > 0 && newProgress < 100) {
            if (progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
            progressBar.setProgress(newProgress);
        } else {
            if (progressBar.getVisibility() != View.INVISIBLE) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String getRawString(int id) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream inputStream = getResources().openRawResource(id);
        byte[] buffer = new byte[1024];
        do {
            int count = inputStream.read(buffer);
            if (count <= 0)
                break;
            bos.write(buffer, 0, count);
        } while (true);
        inputStream.close();

        return bos.toString("UTF-8");
    }

    public byte[] getRawData(int id) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream inputStream = getResources().openRawResource(id);
        byte[] buffer = new byte[1024];
        do {
            int count = inputStream.read(buffer);
            if (count <= 0)
                break;
            bos.write(buffer, 0, count);
        } while (true);
        inputStream.close();

        return bos.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Controller.FILE_SELECTED) {
            if (mUploadHandler != null) {
                mUploadHandler.onResult(resultCode, intent);
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, final int newProgress) {
            if (progressBar != null) {
                MattermostApplication.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        reportProgress(newProgress);
                    }
                });
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i("Console:", consoleMessage.lineNumber() + " " + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }

        public MyWebChromeClient() {
        }

        private String getTitleFromUrl(String url) {
            String title = url;
            try {
                URL urlObj = new URL(url);
                String host = urlObj.getHost();
                if (host != null && !host.isEmpty()) {
                    return urlObj.getProtocol() + "://" + host;
                }
                if (url.startsWith("file:")) {
                    String fileName = urlObj.getFile();
                    if (fileName != null && !fileName.isEmpty()) {
                        return fileName;
                    }
                }
            } catch (Exception e) {
            }

            return title;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            String newTitle = getTitleFromUrl(url);

            new AlertDialog.Builder(WebViewActivity.this).setTitle(newTitle).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }).setCancelable(false).create().show();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            String newTitle = getTitleFromUrl(url);

            new AlertDialog.Builder(WebViewActivity.this).setTitle(newTitle).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).setCancelable(false).create().show();

            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, "", "filesystem");
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadHandler = new UploadHandler(new Controller());
            mUploadHandler.openFileChooser(uploadMsg, acceptType, capture);
        }

        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {

            String acceptTypes[] = fileChooserParams.getAcceptTypes();

            String acceptType = "";
            for (int i = 0; i < acceptTypes.length; ++i) {
                if (acceptTypes[i] != null && acceptTypes[i].length() != 0)
                    acceptType += acceptTypes[i] + ";";
            }
            if (acceptType.length() == 0)
                acceptType = "*/*";

            final ValueCallback<Uri[]> finalFilePathCallback = filePathCallback;

            ValueCallback<Uri> vc = new ValueCallback<Uri>() {

                @Override
                public void onReceiveValue(Uri value) {

                    Uri[] result;
                    if (value != null)
                        result = new Uri[]{value};
                    else
                        result = null;

                    finalFilePathCallback.onReceiveValue(result);

                }
            };

            openFileChooser(vc, acceptType, "filesystem");

            return true;
        }
    }

    class Controller {
        final static int FILE_SELECTED = 4;

        Activity getActivity() {
            return WebViewActivity.this;
        }
    }

    class UploadHandler {
        private ValueCallback<Uri> mUploadMessage;
        private String mCameraFilePath;
        private boolean mHandled;
        private boolean mCaughtActivityNotFoundException;
        private Controller mController;

        public UploadHandler(Controller controller) {
            mController = controller;
        }

        String getFilePath() {
            return mCameraFilePath;
        }

        boolean handled() {
            return mHandled;
        }

        void onResult(int resultCode, Intent intent) {
            if (resultCode == Activity.RESULT_CANCELED && mCaughtActivityNotFoundException) {
                mCaughtActivityNotFoundException = false;
                return;
            }
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                    : intent.getData();

            if (result == null && intent == null && resultCode == Activity.RESULT_OK) {
                File cameraFile = new File(mCameraFilePath);
                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);

                    mController.getActivity().sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }
            mUploadMessage.onReceiveValue(result);
            mHandled = true;
            mCaughtActivityNotFoundException = false;
        }

        void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            final String imageMimeType = "image/*";
            final String videoMimeType = "video/*";
            final String audioMimeType = "audio/*";
            final String mediaSourceKey = "capture";
            final String mediaSourceValueCamera = "camera";
            final String mediaSourceValueFileSystem = "filesystem";
            final String mediaSourceValueCamcorder = "camcorder";
            final String mediaSourceValueMicrophone = "microphone";
            String mediaSource = mediaSourceValueFileSystem;

            if (mUploadMessage != null) {
                return;
            }
            mUploadMessage = uploadMsg;
            String params[] = acceptType.split(";");
            String mimeType = params[0];
            if (capture.length() > 0) {
                mediaSource = capture;
            }
            if (capture.equals(mediaSourceValueFileSystem)) {
                for (String p : params) {
                    String[] keyValue = p.split("=");
                    if (keyValue.length == 2) {
                        if (mediaSourceKey.equals(keyValue[0])) {
                            mediaSource = keyValue[1];
                        }
                    }
                }
            }

            mCameraFilePath = null;
            if (mimeType.equals(imageMimeType)) {
                if (mediaSource.equals(mediaSourceValueCamera)) {
                    startActivity(createCameraIntent());
                    return;
                } else {
                    Intent chooser = createChooserIntent(createCameraIntent());
                    chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(imageMimeType));
                    startActivity(chooser);
                    return;
                }
            } else if (mimeType.equals(videoMimeType)) {
                if (mediaSource.equals(mediaSourceValueCamcorder)) {
                    startActivity(createCamcorderIntent());
                    return;
                } else {
                    Intent chooser = createChooserIntent(createCamcorderIntent());
                    chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(videoMimeType));
                    startActivity(chooser);
                    return;
                }
            } else if (mimeType.equals(audioMimeType)) {
                if (mediaSource.equals(mediaSourceValueMicrophone)) {
                    startActivity(createSoundRecorderIntent());
                    return;
                } else {
                    Intent chooser = createChooserIntent(createSoundRecorderIntent());
                    chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(audioMimeType));
                    startActivity(chooser);
                    return;
                }
            }

            startActivity(createDefaultOpenableIntent());
        }

        private void startActivity(Intent intent) {
            try {
                mController.getActivity().startActivityForResult(intent, Controller.FILE_SELECTED);
            } catch (ActivityNotFoundException e) {
                try {
                    mCaughtActivityNotFoundException = true;
                    mController.getActivity().startActivityForResult(createDefaultOpenableIntent(),
                            Controller.FILE_SELECTED);
                } catch (ActivityNotFoundException e2) {
                    Toast.makeText(mController.getActivity(), R.string.uploads_disabled,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        private Intent createDefaultOpenableIntent() {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            Intent chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(),
                    createSoundRecorderIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, i);

            return chooser;
        }

        private Intent createChooserIntent(Intent... intents) {
            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
            chooser.putExtra(Intent.EXTRA_TITLE,
                    mController.getActivity().getResources()
                            .getString(R.string.choose_upload));

            return chooser;
        }

        private Intent createOpenableIntent(String type) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType(type);

            return i;
        }

        private Intent createCameraIntent() {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File externalDataDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM);
            File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                    File.separator + "browser-photos");
            cameraDataDir.mkdirs();
            mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                    System.currentTimeMillis() + ".jpg";
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));

            return cameraIntent;
        }

        private Intent createCamcorderIntent() {
            return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        }

        private Intent createSoundRecorderIntent() {
            return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        }
    }
}
