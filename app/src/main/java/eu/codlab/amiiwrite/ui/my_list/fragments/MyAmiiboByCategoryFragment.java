package eu.codlab.amiiwrite.ui.my_list.fragments;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.database.controllers.AmiiboFactory;
import eu.codlab.amiiwrite.database.holders.AmiiboIdentifiersHolder;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;

/**
 * Show all amiibo categories
 * <p/>
 * for instance, Classic mario, Bowser, Mr Game And Watch etc...
 * with the number of dumps you have for each
 */
public class MyAmiiboByCategoryFragment
        extends AbstractMyAmiiboFragment<AmiiboIdentifiersHolder> {
    public MyAmiiboByCategoryFragment() {
    }

    public static MyAmiiboByCategoryFragment newInstance() {
        MyAmiiboByCategoryFragment fragment = new MyAmiiboByCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(Container item) {
        //it is a category, so the event information is in identifier
        EventBus.getDefault().post(new EventMyList.EventLoadAmiibos(item.identifier));
    }

    @Override
    protected List<AmiiboIdentifiersHolder> getListOfItem() {
        return AmiiboFactory.getAmiiboDescriptorCache().getListOfIdentifiers();
    }

    @Override
    protected List<Container> itemsToContainers(List<AmiiboIdentifiersHolder> items) {
        List<Container> containers = new ArrayList<>();
        for (AmiiboIdentifiersHolder tuple : items) {
            containers.add(new Container(tuple.identifier, tuple.name, tuple.count));
        }
        return containers;
    }
}
