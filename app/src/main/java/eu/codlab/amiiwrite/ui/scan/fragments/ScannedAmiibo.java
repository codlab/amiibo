package eu.codlab.amiiwrite.ui.scan.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.raizlabs.android.dbflow.data.Blob;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannedAmiibo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannedAmiibo extends StackController.PopableFragment {
    private static final String DATA_SCANNED = "DATA_SCANNED";

    private Amiibo _tmp_amiibo;

    @Bind(R.id.recycler)
    RecyclerView _recycler;

    @OnClick(R.id.fab)
    public void onAddAmiibo() {
        _tmp_amiibo = new Amiibo();
        _tmp_amiibo.data = new Blob(_data);

        requestAmiiboInformation();
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

    private byte[] _data;

    public ScannedAmiibo() {
    }

    public static ScannedAmiibo newInstance(byte[] data) {
        ScannedAmiibo fragment = new ScannedAmiibo();
        Bundle args = new Bundle();
        args.putByteArray(DATA_SCANNED, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _data = getArguments().getByteArray(DATA_SCANNED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanned_amiibo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        _recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        _recycler.setAdapter(new AmiiboAdapter(_data));
    }

    @Override
    public boolean hasParent() {
        return true;
    }
}
