package eu.codlab.amiiwrite.ui.my_list.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import eu.codlab.amiiwrite.ui._stack.PopableFragment;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public abstract class AbstractLoadableFragment<T> extends PopableFragment {

    public AbstractLoadableFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getLayoutToInflate(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        init(view);
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        internalRequestRefreshList();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    protected abstract void requestRefreshList();

    private void internalRequestRefreshList() {
        requestRefreshList();
        EventBus.getDefault().post(new EventMyList.EventFetch(getImplementationClass().getName()));
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void onAskedToSearchCategories(EventMyList.EventFetch event) {
        if (isCorrectEventFetch(event)) {
            EventFetched<T> result = new EventFetched<>(getListOfItem());
            EventBus.getDefault().post(result);
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onFetchedCategories(EventFetched<T> event) {
        List list = event.result;
        if (list != null && list.size() > 0 && isCorrectInstance(list.get(0))) {
            updateUpdate(event);
        }
    }


    @LayoutRes
    protected abstract int getLayoutToInflate();

    protected abstract void init(View view);

    protected abstract void updateUpdate(EventFetched<T> event);

    protected abstract List<T> getListOfItem();

    protected abstract List<Container> itemsToContainers(List<T> items);

    protected abstract boolean isCorrectInstance(Object object);

    protected abstract Class<? extends T> getImplementationClass();

    private boolean isCorrectEventFetch(EventMyList.EventFetch event) {
        return event != null && event.class_name != null
                && event.class_name.equalsIgnoreCase(getImplementationClass().getName());
    }


    protected class EventFetched<T> {
        List<T> result;

        public EventFetched(List<T> result) {
            this.result = result;
        }
    }
}
