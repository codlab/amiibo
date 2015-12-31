package eu.codlab.amiiwrite.ui.information.fragments;


import android.os.Bundle;

import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.database.controllers.AmiiboFactory;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;
import eu.codlab.amiiwrite.ui.scan.ScanEvent;

public class AmiiboInformationFragment extends AbstractAmiiboInformationFragment {
    private final static String AMIIBO_ID = "AMIIBO_ID";

    public static AmiiboInformationFragment newInstance(Amiibo amiibo, boolean add_fab) {
        AmiiboInformationFragment fragment = new AmiiboInformationFragment();
        Bundle args = new Bundle();
        args.putLong(AMIIBO_ID, amiibo.id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onFabClicked() {
        EventBus.getDefault().post(new ScanEvent.StartWriteFragment(getAmiiboId()));
    }

    public AmiiboInformationFragment() {
    }

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_amiibo_information;
    }

    @Override
    public AmiiboAdapter getAdapter() {
        return new AmiiboAdapter(getAmiibo());
    }


    private Amiibo getAmiibo() {
        return AmiiboFactory.getAmiiboCache().getFromKey(getAmiiboId());
    }

    private long getAmiiboId() {
        return getArguments().getLong(AMIIBO_ID, 0);
    }
}
