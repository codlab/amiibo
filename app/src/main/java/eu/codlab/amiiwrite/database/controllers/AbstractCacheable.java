package eu.codlab.amiiwrite.database.controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import eu.codlab.amiiwrite.database.models.AmiiboDescriptor;
import eu.codlab.amiiwrite.database.models.AmiiboDescriptor$Table;

/**
 * Created by kevinleperf on 31/12/15.
 */
public abstract class AbstractCacheable<KEY, T> {
    private LruCache<KEY, T> cache = new LruCache<>(100);

    @Nullable
    protected abstract T getFromKeyInternal(@NonNull KEY key);

    protected abstract void updateCache(@NonNull T object, boolean force);

    @Nullable
    public T getFromKey(@NonNull KEY key) {
        T obtained = getCache().get(key);
        if (obtained == null) {
            obtained = getFromKeyInternal(key);
            if (obtained != null) updateCache(obtained, true);
        }
        return obtained;
    }

    public void updateInDatabase(@NonNull T object) {
        T modified = updateInDatabaseInternal(object);

        updateCache(modified, true);
    }

    @NonNull
    protected abstract T updateInDatabaseInternal(@NonNull T object);

    @NonNull
    protected LruCache<KEY, T> getCache() {
        return cache;
    }
}
