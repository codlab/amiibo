package eu.codlab.amiiwrite.ui.information.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboHelper;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui.information.adapters.internal.BindableViewHolder;
import eu.codlab.amiiwrite.ui.information.adapters.internal.HeaderViewHolder;
import eu.codlab.amiiwrite.ui.information.adapters.internal.PageViewHolder;
import eu.codlab.amiiwrite.ui.information.adapters.internal.PagerHeaderViewHolder;
import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboAdapter extends RecyclerView.Adapter<BindableViewHolder> {
    private final static int VIEWTYPE_HEADER = 0;
    private final static int VIEWTYPE_PAGE_DESCRIPTOR = 1;
    private final static int VIEWTYPE_PAGE = 2;

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
    public BindableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEWTYPE_HEADER:
                return new HeaderViewHolder(inflater
                        .inflate(R.layout.fragment_amiibo_information_header, parent, false));
            case VIEWTYPE_PAGE_DESCRIPTOR:
                return new PagerHeaderViewHolder(inflater
                        .inflate(R.layout.fragment_amiibo_information_pages_header, parent, false));
            case VIEWTYPE_PAGE:
                return new PageViewHolder(inflater
                        .inflate(R.layout.fragment_amiibo_information_page, parent, false));
        }

        return new PagerHeaderViewHolder(inflater
                .inflate(R.layout.fragment_amiibo_information_pages_header, parent, false));
    }

    @Override
    public void onBindViewHolder(BindableViewHolder holder, int position) {
        holder.onBindViewHolder(this, position - holder.getDelta());
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

    public byte[] getObject(int position) {
        return _amiibo_array.get(position);
    }

    public Amiibo getAmiibo() {
        return _amiibo;
    }

    public String getIdentifier() {
        return _amiibo_identifier;
    }
}
