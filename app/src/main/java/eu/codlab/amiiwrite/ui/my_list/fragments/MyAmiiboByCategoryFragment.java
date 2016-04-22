package eu.codlab.amiiwrite.ui.my_list.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.database.controllers.AmiiboFactory;
import eu.codlab.amiiwrite.database.holders.AmiiboIdentifiersHolder;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.AmiiboCategory;
import eu.codlab.amiiwrite.ui.my_list.adapters.internal.Container;
import eu.codlab.amiiwrite.ui.scan.ScanEvent;
import eu.codlab.recyclercolumnadaptable.IRecyclerColumnsListener;
import eu.codlab.recyclercolumnadaptable.RecyclerColumnsWithContentView;
import eu.codlab.recyclercolumnadaptable.inflater.AbstractItemInflater;
import eu.codlab.recyclercolumnadaptable.item.ContentItem;

/**
 * Show all amiibo categories
 * <p/>
 * for instance, Classic mario, Bowser, Mr Game And Watch etc...
 * with the number of dumps you have for each
 */
public class MyAmiiboByCategoryFragment
        extends AbstractLoadableFragment<AmiiboIdentifiersHolder>
        implements IRecyclerColumnsListener, AbstractItemInflater<AmiiboCategory> {


    @OnClick(R.id.fab)
    public void onScanRequested() {
        EventBus.getDefault().post(new ScanEvent.StartFragment());
    }

    @Bind(R.id.grid)
    RecyclerColumnsWithContentView _grid;
    private List<AmiiboIdentifiersHolder> _list;
    private int _tmp_position;
    private boolean _is_already_shown;

    public MyAmiiboByCategoryFragment() {
    }

    public static MyAmiiboByCategoryFragment newInstance() {
        MyAmiiboByCategoryFragment fragment = new MyAmiiboByCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void requestRefreshList() {

    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.fragment_my_amiibo_by_category;
    }

    @Override
    protected void init(View view) {
        //unset fadin
        try {
            Field field = _grid.getClass().getDeclaredField("_recycler");
            field.setAccessible(true);
            RecyclerView v = (RecyclerView) field.get(_grid);
            v.setFadingEdgeLength(0);
            v.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _grid.setRecyclerColumnsListener(this);
        _grid.setRecyclerAdapter(this);
    }

    @Override
    protected void updateUpdate(EventFetched<AmiiboIdentifiersHolder> event) {
        if (_list == null || _list.size() != event.result.size()) {
            _list = event.result;

            ((RecyclerView) _grid.findViewById(R.id.recycler)).getAdapter().notifyDataSetChanged();

            init(null);
            _grid.hideContent();
        }
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

    @Override
    protected boolean isCorrectInstance(Object object) {
        return object != null && object instanceof AmiiboIdentifiersHolder;
    }

    @Override
    protected Class<? extends AmiiboIdentifiersHolder> getImplementationClass() {
        return AmiiboIdentifiersHolder.class;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public boolean managedOnBackPressed() {
        if (_grid.isShowingContent()) {
            _grid.hideContent();
            return true;
        }

        return false;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void onHideContent(ViewGroup content) {
        _is_already_shown = false;
        Fragment fragment = getChildFragmentManager().findFragmentByTag("CONTENT");

        if (fragment != null) {
            getChildFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onShowContent(ViewGroup content) {
        Object object = _list.get(_tmp_position);
        String identifier = "";
        if (object instanceof Amiibo) identifier = ((Amiibo) object).amiibo_identifier;
        else if (object instanceof AmiiboIdentifiersHolder)
            identifier = ((AmiiboIdentifiersHolder) object).identifier;


        MyAmiiboFromCategory fragment = (MyAmiiboFromCategory) getChildFragmentManager().findFragmentByTag("CONTENT");

        if (fragment != null && fragment.isFromIdentifier(identifier)) {
            //TODO REFRESH ?
        } else {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            if (!_is_already_shown)
                transaction.setCustomAnimations(R.anim.slide_in_from_right, 0);

            transaction.replace(content.getId(), MyAmiiboFromCategory.newInstance(identifier),
                    "CONTENT")
                    .commit();
        }
        _is_already_shown = true;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @NonNull
    @Override
    public AmiiboCategory onCreateViewHolder(ViewGroup parent) {
        return new AmiiboCategory(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_amibo_item_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AmiiboCategory holder) {
        final int position = holder.getItem().getPosition();

        final AmiiboIdentifiersHolder tuple = _list.get(position);
        holder.onBindViewHolder(tuple, null);
        holder._clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _tmp_position = position;
                _grid.showContent(_tmp_position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _list != null ? _list.size() : 0;
    }

    @Override
    public ContentItem getContentItemAt(int position) {
        if (position < _list.size()) {
            return new ContentItem(position);
        }
        return null;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    @Override
    public View getHeader(@NonNull ViewGroup parent) {
        return null;
    }

    @Override
    public boolean hasFooter() {
        return false;
    }

    @Override
    public View getFooter(@NonNull ViewGroup parent) {
        return null;
    }

    @Override
    public boolean useAnimation() {
        return true;
    }
}
