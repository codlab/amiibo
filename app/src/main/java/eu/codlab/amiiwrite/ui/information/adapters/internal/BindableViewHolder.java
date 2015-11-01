package eu.codlab.amiiwrite.ui.information.adapters.internal;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import eu.codlab.amiiwrite.ui.information.adapters.AmiiboAdapter;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public abstract class BindableViewHolder extends RecyclerView.ViewHolder {

    public abstract void onBindViewHolder(AmiiboAdapter amiiboAdapter, int position);

    public abstract int getDelta();

    public BindableViewHolder(View itemView) {
        super(itemView);
    }
}