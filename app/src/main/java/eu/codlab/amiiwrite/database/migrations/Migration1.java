package eu.codlab.amiiwrite.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.database.models.Database;
import hugo.weaving.DebugLog;

/**
 * Created by kevinleperf on 07/11/2015.
 */
@Migration(version = 4, databaseName = Database.NAME)
public class Migration1 extends AlterTableMigration<Amiibo> {

    public Migration1() {
        super(Amiibo.class);
    }

    @DebugLog
    @Override
    public void onPreMigrate() {
        // Simple ALTER TABLE migration wraps the statements into a nice builder notation
        addColumn(boolean.class, "synced");
    }
}
