package eu.codlab.amiiwrite.ui.my_list.fragments;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.AmiiboContainer;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;

/**
 * Show all amiibo in a specific category
 *
 * For instance, shows all your Classic Mario dumps
 */
public class MyAmiiboFromCategory extends AbstractMyAmiiboFragment<Amiibo> {
    private final static String AMIIBO_IDENTIFIER = "AMIIBO_IDENTIFIER";

    public MyAmiiboFromCategory() {
    }

    @Override
    protected List<Amiibo> getListOfItem() {
        return AmiiboController.getInstance()
                .getAmiibos(getAmiiboIdentifier());
    }

    @Override
    protected List<Container> itemsToContainers(List<Amiibo> items) {
        List<Container> containers = new ArrayList<>();

        for (Amiibo tuple : items) {
            containers.add(new AmiiboContainer(tuple.amiibo_identifier, tuple.name, tuple.id));
        }
        return containers;
    }

    public static MyAmiiboFromCategory newInstance(String amiibo_id) {
        MyAmiiboFromCategory fragment = new MyAmiiboFromCategory();
        Bundle args = new Bundle();
        args.putString(AMIIBO_IDENTIFIER, amiibo_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(Container item) {
        //it is from an amiibo category, so the relevant information is from the data
        EventBus.getDefault().post(new EventMyList.EventLoadAmiibo(item.data));
    }

    private String getAmiiboIdentifier() {
        return getArguments().getString(AMIIBO_IDENTIFIER, "");
    }
}
