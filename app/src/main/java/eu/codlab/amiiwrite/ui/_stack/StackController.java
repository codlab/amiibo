package eu.codlab.amiiwrite.ui._stack;

import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.amiiwrite.MainActivity;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class StackController {
    public static abstract class PopableFragment extends Fragment {
        public abstract boolean hasParent();

        public abstract boolean managedOnBackPressed();
    }

    private MainActivity _parent;
    private View _container;
    private List<PopableFragment> _fragments;

    private StackController() {

    }

    public StackController(MainActivity parent, View received) {
        _fragments = new ArrayList<>();
        _container = received;
        _parent = parent;
    }

    public void remove(int count) {
        for (int i = 0; i < count; i++)
            if (_fragments.size() > 0)
                _fragments.remove(_fragments.size() - 1);
    }

    public boolean pop() {
        if (_fragments.size() > 1) {
            remove(1);
            setFragment();

            return true;
        }
        return false;
    }

    public void flush(){
        while (pop()){

        }
    }

    public void push(PopableFragment new_fragment) {
        _fragments.add(new_fragment);
        setFragment();
    }

    private void setFragment() {
        _parent.getSupportFragmentManager()
                .beginTransaction()
                .replace(_container.getId(), head())
                .commit();

        _parent.invalidateToolbar();
    }

    public PopableFragment head() {
        if (_fragments.size() > 0)
            return _fragments.get(_fragments.size() - 1);
        return null;
    }

    public boolean hasParent() {
        if (head() != null)
            return head().hasParent();
        return false;
    }

    public boolean managedOnBackPressed(){
        if(head() != null)
            return head().managedOnBackPressed();
        return false;
    }
}
