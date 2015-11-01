package eu.codlab.amiiwrite.ui.my_list.adapters.internal;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;
import eu.codlab.amiiwrite.ui.my_list.adapters.IAmiiboListListener;

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

    @OnClick(R.id.clickable)
    public void onClickableClicked() {
        _listener.onClick(_container);
    }

    @Nullable
    private Container _container;
    private IAmiiboListListener _listener;

    @Override
    public void onBindViewHolder(AmiiboListAdapter parent, int position) {
        _container = parent.getObject(position);
        _listener = parent.getListener();

        boolean is_last = position + 1 >= parent.getItemCount();
        header.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        footer.setVisibility(is_last ? View.VISIBLE : View.GONE);

        name.setText(_container.name);
        if (count != null) count.setText(Long.toString(_container.data));

        int drawable = AmiiboMethods.getAmiiboDrawable(itemView.getContext(), _container.identifier);
        if (drawable != 0) {
            icon.setImageResource(drawable);
        }

    }

    public AmiiboCategory(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
