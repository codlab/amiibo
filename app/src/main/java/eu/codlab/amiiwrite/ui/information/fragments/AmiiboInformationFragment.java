package eu.codlab.amiiwrite.ui.information.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;

public class AmiiboInformationFragment extends StackController.PopableFragment {
    private final static String AMIIBO_ID = "AMIIBO_ID";
    private final static String ADD_APPEND = "ADD_APPEND";


    @Bind(R.id.recycler)
    public RecyclerView _recycler;


    public AmiiboInformationFragment() {
    }

    public static AmiiboInformationFragment newInstance(Amiibo amiibo, boolean add_fab) {
        AmiiboInformationFragment fragment = new AmiiboInformationFragment();
        Bundle args = new Bundle();
        args.putLong(AMIIBO_ID, amiibo.id);
        args.putBoolean(ADD_APPEND, add_fab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_amiibo_information, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        _recycler.setLayoutManager(manager);

        _recycler.setAdapter(new AmiiboAdapter(getAmiibo()));
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("");

    }

    private Amiibo getAmiibo() {
        return AmiiboController.getInstance().getAmiibo(getAmiiboId());
    }

    private long getAmiiboId() {
        Bundle args = getArguments();
        return args != null ? args.getLong(AMIIBO_ID, 0) : 0;
    }

    private boolean showFab(){
        Bundle args = getArguments();
        return args != null ? args.getBoolean(ADD_APPEND, false) : false;
    }


    @Override
    public boolean hasParent() {
        return true;
    }
}
