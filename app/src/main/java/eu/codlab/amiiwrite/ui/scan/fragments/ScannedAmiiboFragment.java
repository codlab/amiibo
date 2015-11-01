package eu.codlab.amiiwrite.ui.scan.fragments;


import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.raizlabs.android.dbflow.data.Blob;

import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;
import eu.codlab.amiiwrite.ui.information.fragments.AbstractAmiiboInformationFragment;

public class ScannedAmiiboFragment extends AbstractAmiiboInformationFragment {
    private static final String DATA_SCANNED = "DATA_SCANNED";

    public static ScannedAmiiboFragment newInstance(byte[] data) {
        ScannedAmiiboFragment fragment = new ScannedAmiiboFragment();
        Bundle args = new Bundle();
        args.putByteArray(DATA_SCANNED, data);
        fragment.setArguments(args);
        return fragment;
    }

    private Amiibo _tmp_amiibo;
    private byte[] _data;

    public ScannedAmiiboFragment() {
    }

    @Override
    protected void onFabClicked() {
        _tmp_amiibo = new Amiibo();
        _tmp_amiibo.data = new Blob(_data);

        requestAmiiboInformation();
    }

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_scanned_amiibo;
    }

    @Override
    public AmiiboAdapter getAdapter() {
        return new AmiiboAdapter(_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _data = getArguments().getByteArray(DATA_SCANNED);
        }
    }

    private void requestAmiiboInformation() {
        if (_tmp_amiibo == null) _tmp_amiibo = new Amiibo();

        int title = _tmp_amiibo.name == null ? R.string.add_amiibo_name_title
                : R.string.add_amiibo_game_name_title;
        int text = _tmp_amiibo.name == null ? R.string.add_amiibo_name_text
                : R.string.add_amiibo_game_name_text;

        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(text)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        String name = materialDialog.getInputEditText().getText().toString();
                        if (_tmp_amiibo != null) {
                            if (_tmp_amiibo.name == null) _tmp_amiibo.name = name;
                            else _tmp_amiibo.game_name = name;
                        }
                        materialDialog.dismiss();

                        //if we still have to request for game information
                        if (_tmp_amiibo != null) {
                            if (_tmp_amiibo.game_name == null) {
                                requestAmiiboInformation();
                            } else {
                                AmiiboController.getInstance().insertAmiibo(_tmp_amiibo);
                                _tmp_amiibo = null;
                            }
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        Toast.makeText(materialDialog.getContext(), R.string.canceled,
                                Toast.LENGTH_SHORT).show();
                        materialDialog.dismiss();
                        _tmp_amiibo = null;
                    }
                }).show();
    }

}
