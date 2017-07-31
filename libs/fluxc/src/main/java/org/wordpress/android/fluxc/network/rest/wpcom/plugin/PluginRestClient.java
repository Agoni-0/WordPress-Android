package org.wordpress.android.fluxc.network.rest.wpcom.plugin;

import android.content.Context;

import com.android.volley.RequestQueue;

import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.BaseWPComRestClient;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PluginRestClient extends BaseWPComRestClient {
    @Inject
    public PluginRestClient(Context appContext, Dispatcher dispatcher, RequestQueue requestQueue,
                              AccessToken accessToken, UserAgent userAgent) {
        super(appContext, dispatcher, requestQueue, accessToken, userAgent);
    }
}
