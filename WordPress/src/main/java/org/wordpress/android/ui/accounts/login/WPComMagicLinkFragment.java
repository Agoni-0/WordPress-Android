package org.wordpress.android.ui.accounts.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.wordpress.rest.RestRequest;

import org.json.JSONObject;
import org.wordpress.android.R;
import org.wordpress.android.BuildConfig;
import org.wordpress.android.WordPress;
import org.wordpress.android.analytics.AnalyticsTracker;
import org.wordpress.android.widgets.WPTextView;

import java.util.HashMap;
import java.util.Map;

public class WPComMagicLinkFragment extends Fragment {

    public static final String EMAIL_KEY = "email";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";
    public static final String ERROR_KEY = "error";

    public interface OnMagicLinkFragmentInteraction {
        void onMagicLinkSent();
        void onEnterPasswordRequested();
    }

    private static final String ARG_EMAIL_ADDRESS = "arg_email_address";

    private WPTextView mMagicLinkButton;
    private String mEmail;
    private OnMagicLinkFragmentInteraction mListener;

    public WPComMagicLinkFragment() {
    }

    public static WPComMagicLinkFragment newInstance(String email) {
        WPComMagicLinkFragment fragment = new WPComMagicLinkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL_ADDRESS, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wpcom_magic_link, container, false);
        mMagicLinkButton = (WPTextView) view.findViewById(R.id.magic_button);
        mMagicLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMagicLinkRequest();
            }
        });

        TextView requestPasswordView = (TextView) view.findViewById(R.id.password_layout);
        requestPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEnterPasswordRequested();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMagicLinkFragmentInteraction) {
            mListener = (OnMagicLinkFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void sendMagicLinkRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(EMAIL_KEY, mEmail);
        params.put(CLIENT_ID_KEY, BuildConfig.OAUTH_APP_ID);
        params.put(CLIENT_SECRET_KEY, BuildConfig.OAUTH_APP_SECRET);

        WordPress.getRestClientUtilsV1_1().sendLoginEmail(params, new RestRequest.Listener() {
            @Override
            public void onResponse(JSONObject response) {
                if (mListener != null) {
                    AnalyticsTracker.track(AnalyticsTracker.Stat.LOGIN_MAGIC_LINK_REQUESTED);
                    mListener.onMagicLinkSent();
                }
            }
        }, new RestRequest.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HashMap<String, String> errorProperties = new HashMap<>();
                errorProperties.put(ERROR_KEY, error.getMessage());
                AnalyticsTracker.track(AnalyticsTracker.Stat.LOGIN_MAGIC_LINK_FAILED, errorProperties);
                Snackbar.make(getView(), R.string.magic_link_unavailable_error_message, Snackbar.LENGTH_SHORT);
                if (mListener != null) {
                    mListener.onEnterPasswordRequested();
                }
            }
        });
    }
}
