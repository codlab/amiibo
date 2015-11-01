package eu.codlab.amiiwrite.ui.my_list.fragments;


import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;

/**
 * Show all amiibo categories
 *
 * for instance, Classic mario, Bowser, Mr Game And Watch etc...
 * with the number of dumps you have for each
 */
public class MyAmiiboByCategoryFragment
        extends AbstractMyAmiiboFragment<AmiiboController.AmiiboIdentifiersTuples> {
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
    protected List<AmiiboController.AmiiboIdentifiersTuples> getListOfItem() {
        return AmiiboController.getInstance().getListOfIdentifiers();
    }

    @Override
    protected List<Container> itemsToContainers(List<AmiiboController.AmiiboIdentifiersTuples> items) {
        List<Container> containers = new ArrayList<>();
        for (AmiiboController.AmiiboIdentifiersTuples tuple : items) {
            containers.add(new Container(tuple.identifier, tuple.identifier, tuple.count));
        }
        return containers;
    }
}
