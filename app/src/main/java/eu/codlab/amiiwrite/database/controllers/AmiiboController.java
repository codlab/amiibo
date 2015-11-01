package eu.codlab.amiiwrite.database.controllers;

import android.database.Cursor;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.database.models.Amiibo$Table;
import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboController {
    public static class AmiiboIdentifiersTuples {
        public String identifier;
        public long count;
    }

    public static final AmiiboController _instance = new AmiiboController();

    public static final AmiiboController getInstance() {
        return _instance;
    }

    private LruCache<Long, Amiibo> _cache = new LruCache<>(500);

    public Amiibo getAmiibo(long id) {
        Amiibo amiibo = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.ID).eq(id))
                .querySingle();
        synchronized (this) {
            if (amiibo != null && _cache.get(amiibo.id) == null) _cache.put(amiibo.id, amiibo);
        }

        return amiibo;
    }

    public List<Amiibo> getAmiibos(String amiibo_identifier) {
        Log.d("MainActivity","amiibos from "+amiibo_identifier);
        List<Amiibo> amiibos = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.AMIIBO_IDENTIFIER).eq(amiibo_identifier.toLowerCase()))
                .orderBy(false, Amiibo$Table.CREATED_AT)
                .queryList();

        for (Amiibo amiibo : amiibos) {
            synchronized (this) {
                if (_cache.get(amiibo.id) == null) _cache.put(amiibo.id, amiibo);
            }
        }

        return amiibos;
    }

    public void insertAmiibo(Amiibo amiibo) {
        byte[] identifiers = AmiiboMethods.amiiboIdentifier(amiibo.data.getBlob());
        amiibo.amiibo_identifier = IO.byteArrayToHexString(identifiers);
        amiibo.amiibo_identifier = amiibo.amiibo_identifier.toLowerCase();
        if (amiibo.uuid == null) amiibo.uuid = UUID.randomUUID().toString();
        amiibo.insert();
    }

    public List<AmiiboIdentifiersTuples> getListOfIdentifiers() {
        try {
            List<AmiiboIdentifiersTuples> list = new ArrayList<>();

            Cursor cursor = new Select(ColumnAlias.column(Amiibo$Table.AMIIBO_IDENTIFIER),
                    ColumnAlias.columnsWithFunction("count", Amiibo$Table.AMIIBO_IDENTIFIER).as("c"))
                    .from(Amiibo.class)
                    .where().groupBy(Amiibo$Table.AMIIBO_IDENTIFIER)
                    .query();
            cursor.moveToFirst();
            int index_count = cursor.getColumnIndex("c");
            int index_id = cursor.getColumnIndex(Amiibo$Table.AMIIBO_IDENTIFIER);

            while (!cursor.isAfterLast()) {
                AmiiboIdentifiersTuples tuple = new AmiiboIdentifiersTuples();
                tuple.identifier = cursor.getString(index_id);
                tuple.count = cursor.getLong(index_count);
                Log.d("MainActivity", "having element " + tuple.count);
                list.add(tuple);

                cursor.moveToNext();
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
