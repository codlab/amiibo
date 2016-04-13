package eu.codlab.amiiwrite.ui.scan.fragments;

import android.content.Context;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.codlab.amiiwrite.MainActivity;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboIO;
import eu.codlab.amiiwrite.ui._stack.StackController;

public class ScanFragment extends StackController.PopableFragment {

    private ScanFragment.IScanListener _scan_listener;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Bind(R.id.please_retry)
    View _please_retry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public boolean managedOnBackPressed() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).startScanning();
    }

    @Override
    public void onPause() {
        ((MainActivity) getActivity()).stopScanning();
        super.onPause();
    }

    @Override
    public void onDetach() {
        _scan_listener = null;
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ScanFragment.IScanListener) {
            _scan_listener = (ScanFragment.IScanListener) context;
        } else {
            throw new IllegalStateException("The parent activity does not implements "
                    + IScanListener.class.getName());
        }
    }

    public void tryReadingAmiibo(NfcA ntag215, byte[] uid) {
        boolean read_successfully = false;
        try {
            ntag215.connect();

            boolean authenticated = AmiiboIO.authenticateAmiibo(ntag215, uid);

            if (authenticated) {
                byte[] read = AmiiboIO.readAmiibo(ntag215);
                if (read != null) {
                    _scan_listener.onScanResult(read);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ntag215.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!read_successfully) {
            _please_retry.setVisibility(View.VISIBLE);
        }
    }

    public interface IScanListener {
        public void onScanResult(byte[] bytes);
    }
}
