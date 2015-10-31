package eu.codlab.amiiwrite.ui.scan;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.information.AmiiboAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannedAmiibo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannedAmiibo extends StackController.PopableFragment {
    private static final String DATA_SCANNED = "DATA_SCANNED";

    @Bind(R.id.recycler)
    RecyclerView _recycler;

    @OnClick(R.id.fab)
    public void onAddAmiibo() {
        //
    }

    private byte[] _data;

    public ScannedAmiibo() {
    }

    public static ScannedAmiibo newInstance(byte[] data) {
        ScannedAmiibo fragment = new ScannedAmiibo();
        Bundle args = new Bundle();
        args.putByteArray(DATA_SCANNED, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _data = getArguments().getByteArray(DATA_SCANNED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanned_amiibo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        _recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        _recycler.setAdapter(new AmiiboAdapter(_data));
    }

    @Override
    public boolean hasParent() {
        return true;
    }
}
