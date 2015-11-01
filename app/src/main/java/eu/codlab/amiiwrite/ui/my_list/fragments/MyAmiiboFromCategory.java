package eu.codlab.amiiwrite.ui.my_list.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;
import eu.codlab.amiiwrite.ui.scan.ScanEvent;
import eu.codlab.amiiwrite.utils.RecyclerViewWithEmpty;

public class MyAmiiboFromCategory extends StackController.PopableFragment
        implements AmiiboListAdapter.IAmiiboListListener {
    private final static String AMIIBO_IDENTIFIER = "AMIIBO_IDENTIFIER";

    @Bind(R.id.empty)
    View _empty;

    @Bind(R.id.recycler)
    RecyclerViewWithEmpty _recycler;

    @OnClick(R.id.fab)
    public void onScanRequested() {
        EventBus.getDefault().post(new ScanEvent.StartFragment());
    }

    public MyAmiiboFromCategory() {
    }

    public static MyAmiiboFromCategory newInstance(String amiibo_id) {
        MyAmiiboFromCategory fragment = new MyAmiiboFromCategory();
        Bundle args = new Bundle();
        args.putString(AMIIBO_IDENTIFIER, amiibo_id);
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
        return inflater.inflate(R.layout.fragment_my_amiibo_by_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        _recycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        _recycler.setEmptyView(_empty);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        refreshAmiibo();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Override
    public boolean hasParent() {
        return true;
    }

    public String getAmiiboIdentifier() {
        return getArguments().getString(AMIIBO_IDENTIFIER, "");
    }

    private void refreshAmiibo() {
        _recycler.setAdapter(new AmiiboListAdapter(this));
        EventBus.getDefault().post(new EventFetchAmiibo(getAmiiboIdentifier()));
    }

    @Override
    public void onClick(AmiiboListAdapter.Container item) {
        //it is from an amiibo category, so the relevant information is from the data
        EventBus.getDefault().post(new EventMyList.EventLoadAmiibo(item.data));
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void onAskedToSearchCategories(EventFetchAmiibo event) {
        List<Amiibo> tuples = AmiiboController.getInstance()
                .getAmiibos(event.identifier);

        List<AmiiboListAdapter.Container> containers = new ArrayList<>();

        for (Amiibo tuple : tuples) {
            containers.add(new AmiiboListAdapter.AmiiboContainer(tuple.amiibo_identifier, tuple.name,
                    tuple.id));
        }


        EventFetchedCategories result = new EventFetchedCategories(containers);

        EventBus.getDefault().post(result);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onFetchedCategories(EventFetchedCategories event) {
        _recycler.setAdapter(new AmiiboListAdapter(this, event.amiibos));
    }

    private class EventFetchAmiibo {
        String identifier;

        public EventFetchAmiibo(String identifier) {
            this.identifier = identifier;
        }
    }

    private class EventFetchedCategories {
        List<AmiiboListAdapter.Container> amiibos;

        public EventFetchedCategories(List<AmiiboListAdapter.Container> amiibos) {
            this.amiibos = amiibos;
        }
    }
}
