package eu.codlab.amiiwrite.ui.information.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboHelper;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.amiitool.AmiitoolFactory;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui.information.adapters.internal.BindableViewHolder;
import eu.codlab.amiiwrite.ui.information.adapters.internal.HeaderViewHolder;
import eu.codlab.amiiwrite.ui.information.adapters.internal.PageViewHolder;
import eu.codlab.amiiwrite.ui.information.adapters.internal.PagerHeaderViewHolder;
import eu.codlab.amiiwrite.utils.IO;
import hugo.weaving.DebugLog;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboAdapter extends RecyclerView.Adapter<BindableViewHolder> {
    private final static int VIEWTYPE_HEADER = 0;
    private final static int VIEWTYPE_PAGE_HEADER_DECRYPTED = 1;
    private final static int VIEWTYPE_PAGE_DESCRIPTOR_DECRYPTED = 2;
    private final static int VIEWTYPE_PAGE_HEADER_ENCRYPTED = 3;
    private final static int VIEWTYPE_PAGE_DESCRIPTOR_ENCRYPTED = 4;
    private static final String TAG = AmiiboAdapter.class.getSimpleName();

    private Amiibo _amiibo;
    private String _amiibo_identifier;
    private SparseArray<byte[]> _amiibo_array_decrypted;
    private SparseArray<byte[]> _amiibo_array_encrypted;

    public AmiiboAdapter(Amiibo amiibo) {
        byte[] data = amiibo.data.getBlob();
        byte[] data_decrypted = amiibo.decrypt();

        _amiibo_array_decrypted = AmiiboHelper.pagesIntoList(data_decrypted);
        _amiibo_array_encrypted = AmiiboHelper.pagesIntoList(data);
        _amiibo_identifier = IO.byteArrayToHexString(AmiiboMethods.amiiboIdentifier(data));
        _amiibo = amiibo;
    }

    public AmiiboAdapter(byte[] amiibo) {
        _amiibo_array_decrypted = AmiiboHelper.pagesIntoList(amiibo);
        _amiibo_array_encrypted = AmiiboHelper.pagesIntoList(AmiitoolFactory.getInstance()
                .unpack(amiibo));
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
            case VIEWTYPE_PAGE_HEADER_DECRYPTED:
                return new PagerHeaderViewHolder(
                        inflater.inflate(R.layout.fragment_amiibo_information_pages_header_decrypted,
                                parent, false));
            case VIEWTYPE_PAGE_HEADER_ENCRYPTED:
                return new PagerHeaderViewHolder(
                        inflater.inflate(R.layout.fragment_amiibo_information_pages_header_encrypted,
                                parent, false));
            case VIEWTYPE_PAGE_DESCRIPTOR_ENCRYPTED:
            case VIEWTYPE_PAGE_DESCRIPTOR_DECRYPTED:
                return new PageViewHolder(inflater.
                        inflate(R.layout.fragment_amiibo_information_page, parent, false));
        }

        return new PagerHeaderViewHolder(inflater
                .inflate(R.layout.fragment_amiibo_information_pages_header_encrypted, parent, false));
    }

    @Override
    public void onBindViewHolder(BindableViewHolder holder, int position) {
        holder.onBindViewHolder(this, position - holder.getDelta());
    }

    @Override
    public int getItemViewType(int position) {
        //if it is the header
        if (position == 0) return VIEWTYPE_HEADER;
        if (position == 1) return VIEWTYPE_PAGE_HEADER_DECRYPTED;

        //if it a position in the encrypted array
        position -= 2;
        if (position < _amiibo_array_decrypted.size()) return VIEWTYPE_PAGE_DESCRIPTOR_DECRYPTED;

        //if it is the header of the decrypted part
        position -= _amiibo_array_decrypted.size();
        if (position == 0) return VIEWTYPE_PAGE_HEADER_ENCRYPTED;

        //if it is a decrypted line
        position = 0;
        if (position < _amiibo_array_encrypted.size()) return VIEWTYPE_PAGE_DESCRIPTOR_ENCRYPTED;
        return -1;
    }

    @Override
    public int getItemCount() {
        return 3 + _amiibo_array_decrypted.size() + _amiibo_array_encrypted.size();
    }

    @DebugLog
    public byte[] getObject(int type, int position) {
        position -= 2; //header and header cat

        Log.d(TAG, "getObject() called with: " + "type = [" + type + "], position = [" + position + "]");
        switch (type) {
            case VIEWTYPE_PAGE_DESCRIPTOR_DECRYPTED:
                return _amiibo_array_decrypted.get(position);
            case VIEWTYPE_PAGE_DESCRIPTOR_ENCRYPTED:
                //retrieve the size of previous and header
                position -= _amiibo_array_decrypted.size() + 1;
                return _amiibo_array_encrypted.get(position);
        }
        return null;
    }

    public Amiibo getAmiibo() {
        return _amiibo;
    }

    public String getIdentifier() {
        return _amiibo_identifier;
    }
}
