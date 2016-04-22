package eu.codlab.amiiwrite.events;

import eu.codlab.amiiwrite.sync.AmiiboFile;

/**
 * Created by kevinleperf on 07/11/2015.
 */
public class AmiiboFileRead {
    public AmiiboFile amiibos_file;
    public long last_file;

    public AmiiboFileRead(AmiiboFile amiibos_file, long last_file) {
        this.amiibos_file = amiibos_file;
        this.last_file = last_file;
    }
}
