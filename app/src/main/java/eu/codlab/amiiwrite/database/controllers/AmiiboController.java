package eu.codlab.amiiwrite.database.controllers;

import android.support.v4.util.LruCache;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.database.models.Amiibo$Table;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboController {
    public static final AmiiboController _instance = new AmiiboController();

    public static final AmiiboController getInstance() {
        return _instance;
    }

    private LruCache<Long, Amiibo> _cache;

    public Amiibo getAmiibo(long id) {
        Amiibo amiibo = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.ID).eq(id))
                .querySingle();
        if (amiibo != null && _cache.get(amiibo.id) == null) _cache.put(amiibo.id, amiibo);

        return amiibo;
    }

    public List<Amiibo> getAmiibos(String amiibo_identifier) {
        List<Amiibo> amiibos = new Select()
                .from(Amiibo.class)
                .where(Condition.column(Amiibo$Table.AMIIBO_IDENTIFIER).eq(amiibo_identifier.toLowerCase()))
                .orderBy(false, Amiibo$Table.CREATED_AT)
                .queryList();

        for (Amiibo amiibo : amiibos) {
            if (_cache.get(amiibo.id) == null) _cache.put(amiibo.id, amiibo);
        }

        return amiibos;
    }
}
