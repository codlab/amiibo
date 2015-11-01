package eu.codlab.amiiwrite.ui.drawer;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.R;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;

public class MenuDrawer extends Fragment {
    public MenuDrawer() {
        // Required empty public constructor
    }

    @OnClick(R.id.my_amiibos)
    public void onClickOnMyAmiibo() {
        EventBus.getDefault().post(new EventMyList.EventLoadCategories());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuDrawer.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuDrawer newInstance(String param1, String param2) {
        MenuDrawer fragment = new MenuDrawer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }
}
