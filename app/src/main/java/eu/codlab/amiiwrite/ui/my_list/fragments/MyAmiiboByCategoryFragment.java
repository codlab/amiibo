package eu.codlab.amiiwrite.ui.my_list.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;
import eu.codlab.amiiwrite.ui.scan.ScanEvent;
import eu.codlab.amiiwrite.utils.RecyclerViewWithEmpty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAmiiboByCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAmiiboByCategoryFragment extends StackController.PopableFragment
        implements AmiiboListAdapter.IAmiiboListListener {

    @Bind(R.id.empty)
    View _empty;

    @Bind(R.id.recycler)
    RecyclerViewWithEmpty _recycler;

    @OnClick(R.id.fab)
    public void onScanRequested() {
        EventBus.getDefault().post(new ScanEvent.StartFragment());
    }

    public MyAmiiboByCategoryFragment() {
    }

    public static MyAmiiboByCategoryFragment newInstance() {
        MyAmiiboByCategoryFragment fragment = new MyAmiiboByCategoryFragment();
        Bundle args = new Bundle();
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
        requestRefreshList();
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

    private void requestRefreshList() {
        _recycler.setAdapter(new AmiiboListAdapter(this));
        EventBus.getDefault().post(new EventFetchCategories());
    }

    @Override
    public void onClick(AmiiboListAdapter.Container item) {
        //it is a category, so the event information is in identifier
        EventBus.getDefault().post(new EventMyList.EventLoadAmiibos(item.identifier));
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void onAskedToSearchCategories(EventFetchCategories event) {
        EventFetchedCategories result = new EventFetchedCategories(AmiiboController.getInstance()
                .getListOfIdentifiers());

        EventBus.getDefault().post(result);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onFetchedCategories(EventFetchedCategories event) {
        List<AmiiboController.AmiiboIdentifiersTuples> tuples = event.categories;
        List<AmiiboListAdapter.Container> containers = new ArrayList<>();

        for (AmiiboController.AmiiboIdentifiersTuples tuple : tuples) {
            containers.add(new AmiiboListAdapter.Container(tuple.identifier, tuple.identifier,
                    tuple.count));
        }

        _recycler.setAdapter(new AmiiboListAdapter(this, containers));
    }

    private class EventFetchCategories {

    }

    private class EventFetchedCategories {
        List<AmiiboController.AmiiboIdentifiersTuples> categories;

        public EventFetchedCategories(List<AmiiboController.AmiiboIdentifiersTuples> categories) {
            this.categories = categories;
        }
    }
}
