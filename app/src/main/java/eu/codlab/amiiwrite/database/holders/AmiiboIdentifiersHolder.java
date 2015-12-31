package eu.codlab.amiiwrite.database.holders;

import android.support.annotation.NonNull;

/**
 * Created by kevinleperf on 31/12/15.
 */
public class AmiiboIdentifiersHolder {
    public String identifier;
    public String name;
    public long count;

    public AmiiboIdentifiersHolder(@NonNull String identifier,
                                   @NonNull String name,
                                   long count) {
        this.identifier = identifier;
        this.count = count;
        this.name = name;
    }
}
