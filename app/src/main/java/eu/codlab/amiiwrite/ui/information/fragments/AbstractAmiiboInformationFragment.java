package eu.codlab.amiiwrite.ui.information.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui._stack.PopableFragment;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public abstract class AbstractAmiiboInformationFragment extends PopableFragment {
    protected abstract int getResourceLayout();

    protected abstract void onFabClicked();

    protected abstract AmiiboAdapter getAdapter();


    @Bind(R.id.recycler)
    public RecyclerView _recycler;

    @Nullable
    @OnClick(R.id.fab)
    public void onClickWriteAmiibo() {
        onFabClicked();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getResourceLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        _recycler.setLayoutManager(manager);

        _recycler.setAdapter(getAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("");
    }

    @Override
    public boolean hasParent() {
        return true;
    }

}
