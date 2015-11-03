package eu.codlab.amiiwrite.events;

import java.util.List;

import eu.codlab.amiiwrite.webservice.models.AmiiboDescriptorInformation;

/**
 * Created by kevinleperf on 03/11/2015.
 */
public class PostRefreshAmiibos {
    public List<AmiiboDescriptorInformation> amiibos;

    public PostRefreshAmiibos(List<AmiiboDescriptorInformation> amiibos) {
        this.amiibos = amiibos;
    }
}
