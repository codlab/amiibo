package eu.codlab.amiiwrite.ui.my_list.adapters.internal;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;

/**
 * Created by kevinleperf on 01/11/2015.
 */
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

    @Bind(R.id.clickable)
    public View _clickable;

    @Nullable
    private Container _container;

    @Override
    public void onBindViewHolder(final AmiiboListAdapter parent, int position) {
        _container = parent.getObject(position);

        updateHeaderFooter(parent, position);

        name.setText(_container.name);
        if (count != null) count.setText(Long.toString(_container.data));

        int drawable = AmiiboMethods.getAmiiboDrawable(itemView.getContext(), _container.identifier);
        if (drawable != 0) {
            icon.setImageResource(drawable);
        }

        _clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent.getListener() != null)
                    parent.getListener().onClick(_container);
            }
        });
    }

    public void onBindViewHolder(AmiiboController.AmiiboIdentifiersTuples parent,
                                 View.OnClickListener listener) {
        name.setText(parent.name);
        if (count != null) count.setText(Long.toString(parent.count));

        int drawable = AmiiboMethods.getAmiiboDrawable(itemView.getContext(), parent.identifier);
        if (drawable != 0) {
            icon.setImageResource(drawable);
        }

        _clickable.setOnClickListener(listener);

        updateHeaderFooter(null, -1);
    }


    protected void updateHeaderFooter(final AmiiboListAdapter parent, int position) {
        header.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);
    }

    public AmiiboCategory(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
