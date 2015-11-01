package eu.codlab.amiiwrite.database.controllers;

import android.database.Cursor;
import android.support.v4.util.LruCache;

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

    public List<Amiibo> getAmiibos(String amiibo_identifier) {
        List<Amiibo> amiibos = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.AMIIBO_IDENTIFIER).eq(amiibo_identifier.toLowerCase()))
                .orderBy(false, Amiibo$Table.CREATED_AT)
                .queryList();

        for (Amiibo amiibo : amiibos) updateCache(amiibo);

        return amiibos;
    }

    public void insertAmiibo(Amiibo amiibo) {
        byte[] identifiers = AmiiboMethods.amiiboIdentifier(amiibo.data.getBlob());

        //set the identifier as the readable representation from the available pages
        amiibo.amiibo_identifier = IO.byteArrayToHexString(identifiers);
        //lower case the amiibo identifier to make research for more efficient
        amiibo.amiibo_identifier = amiibo.amiibo_identifier.toLowerCase();

        //create and uuid from the amiibo, to make easier share/cloud storage
        if (amiibo.uuid == null) amiibo.uuid = UUID.randomUUID().toString();

        amiibo.insert();
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
            AmiiboIdentifiersTuples tuple = new AmiiboIdentifiersTuples(cursor.getString(idx_id),
                    cursor.getLong(idx_count));
            list.add(tuple);
            cursor.moveToNext();
        }


        return list;
    }

    public List<AmiiboDescriptor> getAmiiboDescriptor() {
        return new Select()
                .from(AmiiboDescriptor.class)
                .queryList();
    }


    public static class AmiiboIdentifiersTuples {
        public String identifier;
        public long count;

        public AmiiboIdentifiersTuples(String identifier, long count) {
            this.identifier = identifier;
            this.count = count;
        }
    }

    private void updateCache(Amiibo amiibo) {
        if (amiibo != null && _cache.get(amiibo.id) == null)
            _cache.put(amiibo.id, amiibo);
    }

    private LruCache<Long, Amiibo> _cache = new LruCache<>(500);

}
