package eu.codlab.amiiwrite.database.controllers;

import android.database.Cursor;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.database.models.Amiibo$Table;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor$Table;
import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboController {
    public static final AmiiboController _instance = new AmiiboController();

    public static final AmiiboController getInstance() {
        return _instance;
    }

    public Amiibo getAmiibo(long id) {
        Amiibo amiibo = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.ID).eq(id))
                .querySingle();
        updateCache(amiibo);
        return amiibo;
    }

    public Amiibo getAmiibo(String uuid) {
        uuid = uuid.toLowerCase();
        Amiibo amiibo = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.UUID).eq(uuid))
                .querySingle();
        updateCache(amiibo);
        return amiibo;
    }

    public List<Amiibo> getAmiibos(String amiibo_identifier) {
        List<Amiibo> amiibos = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.AMIIBO_IDENTIFIER).eq(amiibo_identifier.toLowerCase()))
                .orderBy(false, Amiibo$Table.CREATED_AT)
                .queryList();

        for (Amiibo amiibo : amiibos) updateCache(amiibo);

        return amiibos;
    }

    public List<Amiibo> getAmiibosUnsynced() {
        List<Amiibo> amiibos = new Select()
                .from(Amiibo.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(Amiibo$Table.SYNCED).eq(false))
                        .or(Condition.column(Amiibo$Table.SYNCED).isNull()))
                .queryList();

        for (Amiibo amiibo : amiibos) updateCache(amiibo);

        return amiibos;
    }

    public void updateAmiibo(Amiibo amiibo) {
        byte[] identifiers = AmiiboMethods.amiiboIdentifier(amiibo.data.getBlob());

        //set the identifier as the readable representation from the available pages
        amiibo.amiibo_identifier = IO.byteArrayToHexString(identifiers);
        //lower case the amiibo identifier to make research for more efficient
        amiibo.amiibo_identifier = amiibo.amiibo_identifier.toLowerCase();

        //create and uuid from the amiibo, to make easier share/cloud storage
        if (amiibo.uuid == null) amiibo.uuid = UUID.randomUUID().toString();

        amiibo.update();
    }

    public void insertAmiibo(Amiibo amiibo) {
        updateAmiibo(amiibo);
    }

    public List<AmiiboIdentifiersTuples> getListOfIdentifiers() {
        List<AmiiboIdentifiersTuples> list = new ArrayList<>();

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
            AmiiboDescriptor descriptor = getAmiiboDescriptor(identifier);
            if (descriptor != null) amiibo_name = descriptor.name;

            AmiiboIdentifiersTuples tuple = new AmiiboIdentifiersTuples(identifier, amiibo_name,
                    cursor.getLong(idx_count));

            Log.d("AmiiboController", "adding row " + tuple.identifier + " " + tuple.name + " " + tuple.count);
            list.add(tuple);
            cursor.moveToNext();
        }


        return list;
    }

    public void updateAmiiboDescriptor(AmiiboDescriptor descriptor) {
        if (descriptor == null) return;

        AmiiboDescriptor previous = new Select()
                .from(AmiiboDescriptor.class)
                .where(Condition.column(AmiiboDescriptor$Table.AMIIBO_IDENTIFIER)
                        .eq(descriptor.amiibo_identifier.toLowerCase()))
                .querySingle();
        if (previous == null) {
            previous = descriptor;
            descriptor.insert();
        } else {
            previous.amiibo_identifier = descriptor.amiibo_identifier.toLowerCase();
            previous.name = descriptor.name;
            previous.update();
        }

        updateCache(previous, true);

    }

    public AmiiboDescriptor getAmiiboDescriptor(String identifier) {
        AmiiboDescriptor descriptor = _cache_amiibo_descriptor.get(identifier);
        if (descriptor == null) {
            identifier = identifier.toLowerCase();
            descriptor = new Select()
                    .from(AmiiboDescriptor.class)
                    .where(Condition.column(AmiiboDescriptor$Table.AMIIBO_IDENTIFIER).eq(identifier))
                    .querySingle();

            updateAmiiboDescriptor(descriptor);

            Log.d("AmiiboController", "trying to look for " + identifier);
            if (descriptor != null)
                Log.d("AmiiboController", "having description " + descriptor.amiibo_identifier);
        }
        return descriptor;
    }

    public void setUploaded(String uuid, boolean b) {
        Amiibo amiibo = getAmiibo(uuid);
        amiibo.synced = b;
        updateAmiibo(amiibo);
    }


    public static class AmiiboIdentifiersTuples {
        public String identifier;
        public String name;
        public long count;

        public AmiiboIdentifiersTuples(String identifier, String name, long count) {
            this.identifier = identifier;
            this.count = count;
            this.name = name;
        }
    }

    private void updateCache(AmiiboDescriptor amiibo_descriptor, boolean force) {
        if (force ||
                (amiibo_descriptor != null
                        && _cache_amiibo_descriptor.get(amiibo_descriptor.amiibo_identifier) == null))
            _cache_amiibo_descriptor.put(amiibo_descriptor.amiibo_identifier, amiibo_descriptor);
    }

    private void updateCache(Amiibo amiibo) {
        if (amiibo != null && _cache.get(amiibo.id) == null) {
            _cache.put(amiibo.id, amiibo);
        }
        if (amiibo != null && _cache_string.get(amiibo.uuid) == null) {
            _cache_string.put(amiibo.uuid, amiibo);
        }
    }

    private LruCache<String, AmiiboDescriptor> _cache_amiibo_descriptor = new LruCache<>(200);
    private LruCache<Long, Amiibo> _cache = new LruCache<>(500);
    private LruCache<String, Amiibo> _cache_string = new LruCache<>(500);

}
