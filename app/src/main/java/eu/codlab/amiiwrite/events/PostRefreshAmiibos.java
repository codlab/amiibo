package eu.codlab.amiiwrite.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import eu.codlab.amiiwrite.webservice.models.AmiiboDescriptorInformation;

/**
 * Created by kevinleperf on 03/11/2015.
 */
public class PostRefreshAmiibos {
    @Nullable
    public List<AmiiboDescriptorInformation> amiibos;

    public PostRefreshAmiibos(@Nullable List<AmiiboDescriptorInformation> amiibos) {
        this.amiibos = amiibos;
    }
}
