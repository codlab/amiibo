package eu.codlab.amiiwrite.ui.information.adapters.internal;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class HeaderViewHolder extends BindableViewHolder {
    @Bind(R.id.icon)
    public ImageView icon;

    @Bind(R.id.name)
    public TextView name;

    @Override
    public void onBindViewHolder(AmiiboAdapter amiiboAdapter, int position) {
        if (amiiboAdapter.getAmiibo() != null)
            name.setText(amiiboAdapter.getAmiibo().name);
        else
            name.setText(R.string.amiibo_void_name);

        Context context = name.getContext();
        int drawable = AmiiboMethods.getAmiiboDrawable(context, amiiboAdapter.getIdentifier());

        if (drawable != 0) {
            icon.setImageResource(drawable);
        }
    }

    @Override
    public int getDelta() {
        return 0;
    }

    public HeaderViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
