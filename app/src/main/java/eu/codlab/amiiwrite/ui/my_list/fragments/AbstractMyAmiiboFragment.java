package eu.codlab.amiiwrite.ui.my_list.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui._stack.PopableFragment;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;
import eu.codlab.amiiwrite.ui.my_list.adapters.IAmiiboListListener;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;
import eu.codlab.amiiwrite.ui.scan.ScanEvent;
import eu.codlab.amiiwrite.utils.RecyclerViewWithEmpty;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public abstract class AbstractMyAmiiboFragment<T> extends PopableFragment
        implements IAmiiboListListener {

    @Bind(R.id.empty)
    View _empty;

    @Bind(R.id.recycler)
    RecyclerViewWithEmpty _recycler;

    @OnClick(R.id.fab)
    public void onScanRequested() {
        EventBus.getDefault().post(new ScanEvent.StartFragment());
    }

    public AbstractMyAmiiboFragment() {

    }

    @Override
    public boolean hasParent() {
        return true;
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

    private void requestRefreshList() {
        _recycler.setAdapter(new AmiiboListAdapter(this));
        EventBus.getDefault().post(new EventFetch());
    }

    protected void setAdapter(AmiiboListAdapter adapter) {
        _recycler.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void onAskedToSearchCategories(EventFetch event) {
        EventFetched<T> result = new EventFetched<>(getListOfItem());

        EventBus.getDefault().post(result);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onFetchedCategories(EventFetched<T> event) {
        List<Container> containers = itemsToContainers(event.result);

        setAdapter(new AmiiboListAdapter(this, containers));
    }

    protected abstract List<T> getListOfItem();

    protected abstract List<Container> itemsToContainers(List<T> items);

    protected class EventFetch {

    }

    private class EventFetched<T> {
        List<T> result;

        public EventFetched(List<T> result) {
            this.result = result;
        }
    }
}
