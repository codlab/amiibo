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
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui._stack.StackController;

public class ScanToWriteFragment extends StackController.PopableFragment {
    private final static String AMIIBO_ID = "AMIIBO_ID";

    private IScanToWriteListener _scan_listener;

    public ScanToWriteFragment() {
        // Required empty public constructor
    }

    public static ScanToWriteFragment newInstance(Amiibo amiibo) {
        return newInstance(amiibo.id);
    }

    public static ScanToWriteFragment newInstance(long id) {
        ScanToWriteFragment fragment = new ScanToWriteFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(AMIIBO_ID, id);
        fragment.setArguments(arguments);
        return fragment;
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

        if (context instanceof IScanToWriteListener) {
            _scan_listener = (IScanToWriteListener) context;
        } else {
            throw new IllegalStateException("The parent activity does not implements "
                    + IScanToWriteListener.class.getName());
        }
    }

    public void tryWriteAmiibo(NfcA ntag215, byte[] uid) {
        boolean read_successfully = false;
        try {
            ntag215.connect();

            boolean authenticated = AmiiboIO.authenticateAmiibo(ntag215, uid);

            if (authenticated) {
                boolean result = AmiiboIO.writeAmiibo(ntag215, getAmiibo().data.getBlob());

                _scan_listener.onWriteResult(result);
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

    private Amiibo getAmiibo() {
        return AmiiboController.getInstance()
                .getAmiibo(getArguments().getLong(AMIIBO_ID, 0));
    }

    public interface IScanToWriteListener {
        void onWriteResult(boolean written);
    }
}
