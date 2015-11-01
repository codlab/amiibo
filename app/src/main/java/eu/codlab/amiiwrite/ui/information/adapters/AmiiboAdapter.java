package eu.codlab.amiiwrite.ui.information.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboHelper;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboAdapter extends RecyclerView.Adapter<AmiiboAdapter.BindableViewHolder> {
    private final static int VIEWTYPE_HEADER = 0;
    private final static int VIEWTYPE_PAGE_DESCRIPTOR = 1;
    private final static int VIEWTYPE_PAGE = 2;

    public abstract class BindableViewHolder extends RecyclerView.ViewHolder {

        protected abstract void onBindViewHolder(int position);

        public abstract int getDelta();

        public BindableViewHolder(View itemView) {
            super(itemView);
        }
    }

    private Amiibo _amiibo;
    private String _amiibo_identifier;
    private SparseArray<byte[]> _amiibo_array;

    public AmiiboAdapter(Amiibo amiibo) {
        byte[] data = amiibo.data.getBlob();
        _amiibo_array = AmiiboHelper.pagesIntoList(data);
        _amiibo_identifier = IO.byteArrayToHexString(AmiiboMethods.amiiboIdentifier(data));
        _amiibo = amiibo;
    }

    public AmiiboAdapter(byte[] amiibo) {
        _amiibo_array = AmiiboHelper.pagesIntoList(amiibo);
        _amiibo_identifier = IO.byteArrayToHexString(AmiiboMethods.amiiboIdentifier(amiibo));
        _amiibo = null;
    }

    @Override
    public AmiiboAdapter.BindableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEWTYPE_HEADER:
                return new Header(inflater
                        .inflate(R.layout.fragment_amiibo_information_header, parent, false));
            case VIEWTYPE_PAGE_DESCRIPTOR:
                return new PagerHeader(inflater
                        .inflate(R.layout.fragment_amiibo_information_pages_header, parent, false));
            case VIEWTYPE_PAGE:
                return new Page(inflater
                        .inflate(R.layout.fragment_amiibo_information_page, parent, false));
        }

        return new PagerHeader(inflater.inflate(R.layout.fragment_amiibo_information_pages_header, parent, false));
    }

    @Override
    public void onBindViewHolder(AmiiboAdapter.BindableViewHolder holder, int position) {
        holder.onBindViewHolder(position - holder.getDelta());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEWTYPE_HEADER;
        if (position == 1) return VIEWTYPE_PAGE_DESCRIPTOR;
        else return VIEWTYPE_PAGE;
    }

    @Override
    public int getItemCount() {
        return 2 + _amiibo_array.size();
    }

    protected class Header extends BindableViewHolder {
        @Bind(R.id.icon)
        public ImageView icon;

        @Bind(R.id.name)
        public TextView name;

        @Override
        protected void onBindViewHolder(int position) {
            if (_amiibo != null)
                name.setText(_amiibo.name);
            else
                name.setText(R.string.amiibo_void_name);

            Context context = name.getContext();
            int drawable = AmiiboMethods.getAmiiboDrawable(context, _amiibo_identifier);

            if (drawable != 0) {
                icon.setImageResource(drawable);
            }
        }

        @Override
        public int getDelta() {
            return 0;
        }

        public Header(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    private class PagerHeader extends BindableViewHolder {

        @Override
        protected void onBindViewHolder(int position) {

        }

        @Override
        public int getDelta() {
            return 0;
        }

        public PagerHeader(View itemView) {
            super(itemView);
        }
    }

    protected class Page extends BindableViewHolder {
        @Bind(R.id.header)
        public View header;

        @Bind(R.id.data)
        public TextView data;

        @Override
        protected void onBindViewHolder(int position) {
            if (position == 0) header.setVisibility(View.VISIBLE);
            else header.setVisibility(View.GONE);

            data.setText(IO.byteArrayToLoggableHexString(_amiibo_array.get(position)));
        }

        @Override
        public int getDelta() {
            return 2;
        }

        public Page(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
