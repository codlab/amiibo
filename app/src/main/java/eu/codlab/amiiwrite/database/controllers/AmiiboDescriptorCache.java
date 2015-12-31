package eu.codlab.amiiwrite.database.controllers;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.amiiwrite.database.holders.AmiiboIdentifiersHolder;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.database.models.Amiibo$Table;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor$Table;

/**
 * Created by kevinleperf on 31/12/15.
 */
public class AmiiboDescriptorCache extends AbstractCacheable<String, AmiiboDescriptor> {

    @NonNull
    public List<AmiiboIdentifiersHolder> getListOfIdentifiers() {
        List<AmiiboIdentifiersHolder> list = new ArrayList<>();

        Cursor cursor = new Select(ColumnAlias.column(Amiibo$Table.AMIIBO_IDENTIFIER),
                ColumnAlias.columnsWithFunction("count", Amiibo$Table.AMIIBO_IDENTIFIER).as("c"))
                .from(Amiibo.class)
                .where().groupBy(Amiibo$Table.AMIIBO_IDENTIFIER)
                .query();
        cursor.moveToFirst();
        int idx_count = cursor.getColumnIndex("c");
        int idx_id = cursor.getColumnIndex(Amiibo$Table.AMIIBO_IDENTIFIER);

        while (!cursor.isAfterLast()) {
            //change the identifier 00000000000 into the "human readable name"
            //not done into the sql query since the controller will cache the descriptor for later use
            String identifier = cursor.getString(idx_id);
            String amiibo_name = identifier;
            AmiiboDescriptor descriptor = getFromKey(identifier);
            if (descriptor != null) amiibo_name = descriptor.name;

            AmiiboIdentifiersHolder tuple = new AmiiboIdentifiersHolder(identifier, amiibo_name,
                    cursor.getLong(idx_count));

            list.add(tuple);
            cursor.moveToNext();
        }


        return list;
    }

    @Override
    @Nullable
    protected AmiiboDescriptor getFromKeyInternal(@NonNull String identifier) {
        AmiiboDescriptor descriptor = null;
        identifier = identifier.toLowerCase();
        descriptor = new Select()
                .from(AmiiboDescriptor.class)
                .where(Condition.column(AmiiboDescriptor$Table.AMIIBO_IDENTIFIER).eq(identifier))
                .querySingle();
        return descriptor;
    }

    @Override
    protected void updateCache(@NonNull AmiiboDescriptor amiibo_descriptor,
                            boolean force) {
        String amiibo_identifier = amiibo_descriptor.amiibo_identifier;
        if (force || null == getCache().get(amiibo_identifier))
            getCache().put(amiibo_identifier, amiibo_descriptor);
    }

    @NonNull
    @Override
    protected AmiiboDescriptor updateInDatabaseInternal(@NonNull AmiiboDescriptor object) {
        AmiiboDescriptor previous = new Select()
                .from(AmiiboDescriptor.class)
                .where(Condition.column(AmiiboDescriptor$Table.AMIIBO_IDENTIFIER)
                        .eq(object.amiibo_identifier.toLowerCase()))
                .querySingle();
        if (previous == null) {
            previous = object;
            object.insert();
        } else {
            previous.amiibo_identifier = object.amiibo_identifier.toLowerCase();
            previous.name = object.name;
            previous.update();
        }
        return previous;
    }
}
