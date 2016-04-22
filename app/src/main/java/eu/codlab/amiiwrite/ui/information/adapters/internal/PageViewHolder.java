package eu.codlab.amiiwrite.ui.information.adapters.internal;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;
import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class PageViewHolder extends BindableViewHolder {
    @Bind(R.id.header)
    public View header;

    @Bind(R.id.data)
    public TextView data;

    @Override
    public void onBindViewHolder(AmiiboAdapter amiiboAdapter, int position) {
        if (position == 0) header.setVisibility(View.VISIBLE);
        else header.setVisibility(View.GONE);

        byte[] bytes = amiiboAdapter.getObject(getItemViewType(), position);
        data.setText(IO.byteArrayToLoggableHexString(bytes));
    }

    @Override
    public int getDelta() {
        return 0;
    }

    public PageViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
