package eu.codlab.amiiwrite.ui.my_list.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class AmiiboListAdapter extends RecyclerView.Adapter<AmiiboListAdapter.LoadableHolder> {
    private final static int AMIIBO_CATEGORY = 0;
    private final static int AMIIBO = 1;
    private final static int AMIIBO_LOADER = 2;

    public interface IAmiiboListListener {
        void onClick(Container item);
    }

    public static class Container {
        public String name;
        public String identifier;
        public long data;

        public Container(String identifier, String name, long data) {
            this.name = name;
            this.identifier = identifier;
            this.data = data;
        }
    }

    public static class AmiiboContainer extends Container {

        public AmiiboContainer(String identifier, String name, long id) {
            super(identifier, name, id);
        }
    }

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
        holder.onBindViewHolder(position);
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

    public class LoadableHolder extends RecyclerView.ViewHolder {
        public LoadableHolder(View itemView) {
            super(itemView);
        }

        protected void onBindViewHolder(int position) {
        }
    }

    public class AmiiboCategory extends LoadableHolder {

        @Bind(R.id.header)
        public View header;

        @Bind(R.id.footer)
        public View footer;

        @Bind(R.id.name)
        public TextView name;

        @Bind(R.id.icon)
        public ImageView icon;

        @Nullable
        @Bind(R.id.count)
        public TextView count;

        @OnClick(R.id.clickable)
        public void onClickableClicked() {
            _listener.onClick(_containers.get((int) getPos()));
        }

        long position;

        private long getPos() {
            return position;
        }

        protected void onBindViewHolder(int position) {
            Container container = _containers.get(position);

            this.position = position;
            if (position + 1 >= getItemCount()) footer.setVisibility(View.VISIBLE);
            else footer.setVisibility(View.GONE);

            if (position == 0) header.setVisibility(View.VISIBLE);
            else header.setVisibility(View.GONE);

            name.setText(container.name);
            if (count != null) count.setText(Long.toString(container.data));

            int drawable = AmiiboMethods.getAmiiboDrawable(itemView.getContext(),
                    _containers.get(position).identifier);
            if (drawable != 0) {
                icon.setImageResource(drawable);
            }
        }

        public AmiiboCategory(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AmiiboItem extends AmiiboCategory {
        public AmiiboItem(View itemView) {
            super(itemView);
        }
    }
}
