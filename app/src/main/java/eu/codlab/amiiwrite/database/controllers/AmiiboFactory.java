package eu.codlab.amiiwrite.database.controllers;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.database.holders.AmiiboIdentifiersHolder;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.database.models.Amiibo$Table;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor$Table;
import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class AmiiboFactory {
    private static AmiiboCache _amiibo_cache = new AmiiboCache();
    private static AmiiboDescriptorCache _amiibo_descriptor_cache = new AmiiboDescriptorCache();

    @NonNull
    public static AmiiboCache getAmiiboCache() {
        return _amiibo_cache;
    }

    @NonNull
    public static AmiiboDescriptorCache getAmiiboDescriptorCache() {
        return _amiibo_descriptor_cache;
    }
}
