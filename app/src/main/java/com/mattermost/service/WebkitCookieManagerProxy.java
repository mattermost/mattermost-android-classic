/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.service;

import android.webkit.ValueCallback;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WebkitCookieManagerProxy extends CookieManager {

    private android.webkit.CookieManager webkitCookieManager;

    public WebkitCookieManagerProxy() {
        this(null, null);

        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        java.net.CookieHandler.setDefault(this);
    }

    public WebkitCookieManagerProxy(CookieStore store, CookiePolicy cookiePolicy) {
        super(null, cookiePolicy);

        this.webkitCookieManager = android.webkit.CookieManager.getInstance();
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        if ((uri == null) || (responseHeaders == null)) return;

        String url = uri.toString();
        for (String headerKey : responseHeaders.keySet()) {
            if ((headerKey == null) || !(headerKey.equalsIgnoreCase("Set-Cookie2") || headerKey.equalsIgnoreCase("Set-Cookie")))
                continue;

            for (String headerValue : responseHeaders.get(headerKey)) {
                this.webkitCookieManager.setCookie(url, headerValue);
            }
        }
    }

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        if ((uri == null) || (requestHeaders == null))
            throw new IllegalArgumentException("Argument is null");

        String url = uri.toString();
        Map<String, List<String>> res = new java.util.HashMap<String, List<String>>();
        String cookie = this.webkitCookieManager.getCookie(url);
        if (cookie != null) res.put("Cookie", Arrays.asList(cookie));

        return res;
    }

    public void clear() {
        this.webkitCookieManager.removeAllCookie();
    }

    @Override
    public CookieStore getCookieStore() {
        throw new UnsupportedOperationException();
    }
}