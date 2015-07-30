package com.github.filipebezerra.baseplayservices.appcompatactivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import com.github.filipebezerra.baseplayservices.delegates.GoogleApiConnectionCallbacksDelegate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import java.util.List;
import timber.log.Timber;

import static com.github.filipebezerra.baseplayservices.connectionmessages.CommonGoogleApiConnectionErrorMessages.getConnectionResultErrorMessage;
import static com.github.filipebezerra.baseplayservices.connectionmessages.CommonGoogleApiConnectionErrorMessages.getConnectionSuspendedCauseMessage;

/**
 * Base {@link AppCompatActivity} with facilities for connecting to Google Play Services across
 * {@link GoogleApiClient}. This class also provides:
 * <ul>
 *     <li>Logging facilities with {@link Timber}</li>
 *     <li>View binding with {@link ButterKnife}</li>
 *     <li>Auto setup for {@link Toolbar} as {@link ActionBar} with Up indicator for navigation</li>
 * </ul>
 *
 * @author Filipe Bezerra
 * @version #, 27/07/2015
 * @since #
 */
public abstract class BaseGoogleApiAppCompatActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    /**
     * Request code to use when launching the resolution activity
     */
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    /**
     * Unique tag for the error dialog fragment
     */
    private static final String DIALOG_ERROR = "dialog_error";
    public static final int NO_ACTION_BAR = 0;
    public static final int NO_UP_INDICATOR = 0;

    /**
     * Track whether the app is already resolving an error
     */
    private boolean mResolvingError = false;

    private static final String STATE_RESOLVING_ERROR_KEY = "resolving_error";

    /**
     * Google Play Services client interface, to invoking any Google API.
     */
    protected GoogleApiClient mGoogleApiClient;
    
    /**
     * Descendant classes must provide their own layout resource.
     *
     * @return the layout resource id.
     */
    protected abstract @LayoutRes int getContentLayoutResId();

    /**
     * Set up the {@link Toolbar} as the {@link ActionBar}. By default this method always
     * returns 0, meaning that ActionBar it's not present, so descendant classes must
     * provide their own {@link Toolbar} widget to properly sets up the widget.
     *
     * @return the resource id
     */
    protected @IdRes int getToolbarResId() {
        return 0;
    }

    /**
     * Set up the Up indicator navigation for the {@link Toolbar} if present. By default this
     * method always returns 0, meaning that Up indicator it's not present in the {@link Toolbar},
     * so descendant classes must provide their own up indicator to properly sets up the up
     * indicator as the home navigation.
     *
     * @return the drawable resource id.
     */
    protected @DrawableRes int getUpIndicatorDrawableResId() {
        return 0;
    }

    /**
     * Unique tag identifier for the class or global identifier for the application.
     *
     * @return the string tag.
     */
    protected abstract String getTag();

    /**
     * Provide a list of {@link Api} to {@link GoogleApiClient}. Descendant classes must provide
     * their own handled Apis.
     *
     * @return a list of used Apis.
     */
    @NonNull protected abstract List<Api<Api.ApiOptions.NoOptions>> getUsedGoogleApis();

    /**
     * Provide the list of {@link Scope} available in {@link Scopes}, to {@link GoogleApiClient}.
     * Descendant classes must provide their own handled Scopes.
     *
     * @return a list of used Scopes.
     */
    @Nullable protected List<Scope> getUsedScopes() {
        return null;
    }

    private GoogleApiConnectionCallbacksDelegate mConnectionCallbacksDelegate;

    /**
     * Build the {@link #mGoogleApiClient} using Apis provided by {@link #getUsedGoogleApis()} and
     * the Scopes provided by {@link #getUsedScopes()}.
     *
     * @see #getUsedGoogleApis()
     * @see #getUsedScopes()
     * @see #onConnected(Bundle)
     * @see #onConnectionSuspended(int)
     * @see #onConnectionFailed(ConnectionResult)
     */
    protected synchronized void buildGoogleApiClient() {
        final GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);

        for (Api usedApi : getUsedGoogleApis()) {
            builder.addApi(usedApi);
        }

        if (getUsedScopes() != null) {
            for (Scope usedScope : getUsedScopes()) {
                builder.addScope(usedScope);
            }
        }

        mGoogleApiClient = builder.build();
    }

    /**
     * Provide basic configurations like: <u> <li>setting up the provided layout as content
     * view</li> <li>binding the views with {@link ButterKnife#bind(Activity)}</li> <li>providing
     * log facilities with {@link Timber#tag(String)}</li> <li>setting up the {@link Toolbar} if
     * present as {@link ActionBar}</li> <li>building up the {@link #mGoogleApiClient} to connect to
     * Google Play Services and accessing the Apis</li> </u>
     *
     * @see #getContentLayoutResId()
     * @see #getTag()
     * @see #setupToolbarAsActionBar()
     * @see #getUpIndicatorDrawableResId()
     * @see #buildGoogleApiClient()
     * @see #getUsedGoogleApis()
     * @see #getUsedScopes()
     */
    @Override protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(getContentLayoutResId());
        ButterKnife.bind(this);
        Timber.tag(getTag());
        setupToolbarAsActionBar();
        buildGoogleApiClient();
        restoreSavedState(inState);
        mConnectionCallbacksDelegate = new GoogleApiConnectionCallbacksDelegate();
    }

    protected void restoreSavedState(Bundle inState) {
        if (inState != null) {
            if (inState.keySet().contains(STATE_RESOLVING_ERROR_KEY)) {
                mResolvingError = inState.getBoolean(STATE_RESOLVING_ERROR_KEY, false);
            }
        }
    }

    @Override protected void onStart() {
        super.onStart();
        if (!isGoogleApiClientConnectedOrConnecting()) {
            if (!mResolvingError) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override protected void onStop() {
        if (isGoogleApiClientConnectedOrConnecting()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public boolean isGoogleApiClientConnectedOrConnecting() {
        return mGoogleApiClient != null &&
                (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected());
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    /**
     * Set up the {@link Toolbar} if present in the provided {@link #getContentLayoutResId()} as
     * the {@link ActionBar}. Also sets the Up indicator for navigation if provided by {@link
     * #getUpIndicatorDrawableResId()}.
     */
    private void setupToolbarAsActionBar() {
        if (getToolbarResId() != NO_ACTION_BAR) {
            final Toolbar toolbar = ButterKnife.findById(this, getToolbarResId());
            if (toolbar != null) {
                setSupportActionBar(toolbar);

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    if (getUpIndicatorDrawableResId() != NO_UP_INDICATOR) {
                        actionBar.setHomeAsUpIndicator(getUpIndicatorDrawableResId());
                        actionBar.setDisplayHomeAsUpEnabled(true);
                    }
                }
            }
        }
    }

    /**
     * Callback after calling {@link GoogleApiClient#connect()} from {@link #mGoogleApiClient}.
     *
     * After calling {@link GoogleApiClient#connect()}, this method will be invoked
     * asynchronously when the connect request has successfully completed. After this
     * callback, the application can make requests on other methods provided by the
     * client and expect that no user intervention is required to call methods that
     * use account and scopes provided to the client constructor.
     *
     * @param connectionHint Bundle of data provided to clients by Google Play services. May be null
     * if no content is provided by the service.
     */
    @Override public void onConnected(Bundle connectionHint) {
        Timber.i("Successfully connected to Google Play Services");
        mConnectionCallbacksDelegate.notifyOnConnected(connectionHint);
    }

    /**
     * Callback after calling {@link GoogleApiClient#connect()} from {@link #mGoogleApiClient}.
     *
     * Called when the client is temporarily in a disconnected state. This can happen if
     * there is a problem with the remote service (e.g. a crash or resource problem causes
     * it to be killed by the system). When called, all requests have been canceled and no
     * outstanding listeners will be executed. GoogleApiClient will automatically attempt
     * to restore the connection. Applications should disable UI components that require
     * the service, and wait for a call to {@link #onConnected(Bundle)} to re-enable them.
     *
     * @param cause The reason for the disconnection. Defined by constants CAUSE_*.
     */
    @Override public void onConnectionSuspended(int cause) {
        Timber.i("Connection with Google Play Services has been suspended");
        final String causeMessage = getConnectionSuspendedCauseMessage(cause);
        Timber.i("The cause: %s", causeMessage);
        mConnectionCallbacksDelegate.notifyOnConnectionSuspended(cause, causeMessage);
    }

    /**
     * Callback after calling {@link GoogleApiClient#connect()} from {@link #mGoogleApiClient}.
     *
     * Called when there was an error connecting the client to the service.
     *
     * @param result A {@link ConnectionResult} that can be used for resolving
     * the error, and deciding what sort of error occurred. To resolve the error,
     * the resolution must be started from an activity with a non-negative requestCode
     * passed to {@link ConnectionResult#startResolutionForResult(Activity, int)}.
     * Applications should implement {@link #onActivityResult(int, int, Intent)} in their
     * Activity to call {@link GoogleApiClient#connect()} again if the user has resolved
     * the issue (resultCode is {@link Activity#RESULT_OK}).
     */
    @Override public void onConnectionFailed(ConnectionResult result) {
        Timber.i("Connection with Google Play Services failed");
        Timber.i("The error message: %s", getConnectionResultErrorMessage(result.getErrorCode()));

        if (!mResolvingError) {
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                    mResolvingError = true;
                } catch (IntentSender.SendIntentException e) {
                    Timber.e(e, "There was an error with the resolution intent. Trying again");
                    mResolvingError = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                showErrorDialog(result.getErrorCode());
                mResolvingError = true;
            }
        }

        mConnectionCallbacksDelegate.notifyOnConnectionFailed(result, mResolvingError);
    }

    /**
     * Creates a dialog for an error message
     * @param errorCode error code from {@link ConnectionResult#getErrorCode()}
     */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (! isGoogleApiClientConnectedOrConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR_KEY, mResolvingError);
    }

    /**
     * Called from ErrorDialogFragment when the dialog is dismissed
     */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            final int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(),
                    REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((BaseGoogleApiAppCompatActivity)getActivity()).onDialogDismissed();
        }
    }

}
