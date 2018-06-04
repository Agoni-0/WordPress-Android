package org.wordpress.android.fluxc.network.rest.wpcom.reader;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.generated.ReaderActionBuilder;
import org.wordpress.android.fluxc.generated.endpoint.WPCOMREST;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.BaseWPComRestClient;
import org.wordpress.android.fluxc.network.rest.wpcom.WPComGsonRequest;
import org.wordpress.android.fluxc.network.rest.wpcom.WPComGsonRequest.WPComErrorListener;
import org.wordpress.android.fluxc.network.rest.wpcom.WPComGsonRequest.WPComGsonNetworkError;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.store.ReaderStore.ReaderError;
import org.wordpress.android.fluxc.store.ReaderStore.ReaderErrorType;
import org.wordpress.android.fluxc.store.ReaderStore.ReaderSearchSitesResponsePayload;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.UrlUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class ReaderRestClient extends BaseWPComRestClient {
    public static final int NUM_SEARCH_RESULTS = 20;

    public ReaderRestClient(Context appContext, Dispatcher dispatcher,
                            RequestQueue requestQueue,
                            AccessToken accessToken,
                            UserAgent userAgent) {
        super(appContext, dispatcher, requestQueue, accessToken, userAgent);
    }

    public void searchReaderSites(@NonNull final String searchTerm, final int offset) {
        String url = WPCOMREST.read.feed.getUrlV1_1();

        Map<String, String> params = new HashMap<>();
        params.put("offset", Integer.toString(offset));
        params.put("exclude_followed", Boolean.toString(true));
        params.put("sort", "relevance");
        params.put("number", Integer.toString(NUM_SEARCH_RESULTS));
        params.put("q", UrlUtils.urlEncode(searchTerm));

        WPComGsonRequest request = WPComGsonRequest.buildGetRequest(url, params, ReaderSearchSitesResponse.class,
                new Response.Listener<ReaderSearchSitesResponse>() {
                    @Override
                    public void onResponse(ReaderSearchSitesResponse response) {
                        boolean canLoadMore = response.feeds.size() == NUM_SEARCH_RESULTS;
                        ReaderSearchSitesResponsePayload payload =
                                new ReaderSearchSitesResponsePayload(
                                        response.feeds,
                                        searchTerm,
                                        offset,
                                        canLoadMore);
                        mDispatcher.dispatch(ReaderActionBuilder.newReaderSearchedSitesAction(payload));
                    }
                }, new WPComErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull WPComGsonNetworkError error) {
                        AppLog.e(AppLog.T.MEDIA, "VolleyError searching reader sites: " + error);
                        ReaderError readerError = new ReaderError(
                                ReaderErrorType.fromBaseNetworkError(error), error.message);
                        ReaderSearchSitesResponsePayload payload =
                                new ReaderSearchSitesResponsePayload(readerError, searchTerm);
                        mDispatcher.dispatch(ReaderActionBuilder.newReaderSearchedSitesAction(payload));
                    }
                }
                                                                   );
        add(request);
    }
}
