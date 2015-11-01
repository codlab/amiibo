package eu.codlab.amiiwrite.ui.my_list.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.AmiiboCategory;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.AmiiboContainer;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.AmiiboItem;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.LoadableHolder;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class AmiiboListAdapter extends RecyclerView.Adapter<LoadableHolder> {
    private final static int AMIIBO_CATEGORY = 0;
    private final static int AMIIBO = 1;
    private final static int AMIIBO_LOADER = 2;

    private List<Container> _containers;
    private IAmiiboListListener _listener;


    public AmiiboListAdapter(IAmiiboListListener listener) {
        _listener = listener;
        _containers = null;
    }

    public AmiiboListAdapter(IAmiiboListListener listener, List<Container> container) {
        this(listener);
        _containers = container;
    }

    public Container getObject(int position) {
        return _containers.get(position);
    }

    public IAmiiboListListener getListener() {
        return _listener;
    }

    @Override
    public LoadableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case AMIIBO_CATEGORY:
                return new AmiiboCategory(inflater.inflate(R.layout.fragment_my_amibo_item_with_count, parent, false));
            case AMIIBO:
                return new AmiiboItem(inflater.inflate(R.layout.fragment_my_amibo_item, parent, false));
            case AMIIBO_LOADER:
                return new LoadableHolder(inflater.inflate(R.layout.fragment_my_amibo_loading, parent, false));
            default:
        }
        return null;
    }

    @Override
    public void onBindViewHolder(LoadableHolder holder, int position) {
        holder.onBindViewHolder(this, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (_containers == null) return AMIIBO_LOADER;
        if (_containers.get(position) instanceof AmiiboContainer) return AMIIBO;
        return AMIIBO_CATEGORY;
    }

    @Override
    public int getItemCount() {
        if (_containers == null) return 1;
        return _containers.size();
    }
}
