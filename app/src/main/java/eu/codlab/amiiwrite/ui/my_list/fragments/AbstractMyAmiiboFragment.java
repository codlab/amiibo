package eu.codlab.amiiwrite.ui.my_list.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;
import eu.codlab.amiiwrite.ui.my_list.adapters.IAmiiboListListener;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;
import eu.codlab.amiiwrite.utils.RecyclerViewWithEmpty;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public abstract class AbstractMyAmiiboFragment<T> extends AbstractLoadableFragment<T>
        implements IAmiiboListListener {

    @Bind(R.id.empty)
    View _empty;

    @Bind(R.id.recycler)
    RecyclerViewWithEmpty _recycler;

    public AbstractMyAmiiboFragment() {

    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.fragment_my_amiibo_from_category;
    }

    @Override
    protected void init(View view) {
        _recycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        _recycler.setEmptyView(_empty);
    }

    @Override
    protected void requestRefreshList() {
        _recycler.setAdapter(new AmiiboListAdapter(this));
    }

    protected void setAdapter(AmiiboListAdapter adapter) {
        _recycler.setAdapter(adapter);
    }

    @Override
    protected void updateUpdate(EventFetched<T> event) {
        List<Container> containers = itemsToContainers(event.result);

        setAdapter(new AmiiboListAdapter(this, containers));
    }
}
