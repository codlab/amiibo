package eu.codlab.amiiwrite.ui.my_list.adapters.internal;

import android.view.View;

import eu.codlab.amiiwrite.ui.my_list.adapters.AmiiboListAdapter;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class AmiiboItem extends AmiiboCategory {
    public AmiiboItem(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateHeaderFooter(AmiiboListAdapter parent, int position) {
        boolean is_last = position + 1 >= parent.getItemCount();

        header.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        footer.setVisibility(is_last ? View.VISIBLE : View.GONE);

    }
}
