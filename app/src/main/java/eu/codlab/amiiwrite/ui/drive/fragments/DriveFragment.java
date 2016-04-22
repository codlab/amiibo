package eu.codlab.amiiwrite.ui.drive.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import eu.codlab.amiiwrite.MainActivity;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.events.SyncResult;
import eu.codlab.amiiwrite.sync.SyncService;
import eu.codlab.amiiwrite.ui._stack.PopableFragment;
import eu.codlab.amiiwrite.ui._stack.StackController;
import hugo.weaving.DebugLog;

public class DriveFragment extends PopableFragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 2976866;

    private GoogleApiClient mGoogleApiClient;

    public DriveFragment() {
        // Required empty public constructor
    }

    @Bind(R.id.start_sync)
    Button _start_sync;

    @Bind(R.id.progress)
    ProgressBar _progress_bar;

    @OnClick(R.id.start_sync)
    public void onStartSyncClicked() {
        if (!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.connect();
        } else if (mGoogleApiClient.isConnected()) {
            ((MainActivity) getActivity()).startSync();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drive_sync, container, false);
    }

    @DebugLog
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @DebugLog
    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public boolean managedOnBackPressed() {
        return false;
    }

    @DebugLog
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        if (SyncService.getInstance() != null)
            mGoogleApiClient = SyncService.getInstance().getClient();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            SyncService.getInstance().setGoogleApiClient(mGoogleApiClient);
        }

    }

    @DebugLog
    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    /* * * * * * * * * * * * * * * * * * * * * *
     *
     * * * * * * * * * * * * * * * * * * * * * */


    @DebugLog
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(),
                        RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil
                    .getErrorDialog(connectionResult.getErrorCode(), getActivity(), 0).show();
        }
    }

    @DebugLog
    public boolean onResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                    return true;
                }
                break;
        }
        return false;
    }

    @DebugLog
    @Override
    public void onConnected(Bundle bundle) {
        SyncService.getInstance().setGoogleApiClient(mGoogleApiClient);
        SyncService.getInstance().init();

        _progress_bar.setVisibility(View.VISIBLE);
        _start_sync.setVisibility(View.GONE);
    }

    @DebugLog
    @Override
    public void onConnectionSuspended(int i) {
    }

    /* * * * * * * * * * * * * * * * * * * * * *
     *
     * * * * * * * * * * * * * * * * * * * * * */

    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void onEvent(SyncResult event) {
        if (event.finished) {
            _progress_bar.setVisibility(View.GONE);
            _start_sync.setVisibility(View.VISIBLE);
        } else {
            _progress_bar.setVisibility(View.VISIBLE);
            _start_sync.setVisibility(View.GONE);
        }
    }
}
