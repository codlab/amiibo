package eu.codlab.amiiwrite.ui.my_list.adapters.internal;

import android.util.Log;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class Container {
    public String name;
    public String identifier;
    public long data;

    public Container(String identifier, String name, long data) {
        Log.d("Container", "creating container " + identifier + " " + name + " " + data);
        this.name = name;
        this.identifier = identifier;
        this.data = data;
    }
}
