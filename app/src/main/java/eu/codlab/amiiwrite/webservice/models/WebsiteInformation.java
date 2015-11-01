package eu.codlab.amiiwrite.webservice.models;

import java.util.List;

/**
 * Describe all the relevant information from the server
 * <p/>
 * Created by kevinleperf on 01/11/2015.
 */
public class WebsiteInformation {
    public long revision;

    public List<AmiiboDescriptorInformation> amiibos;

    public Apk apk;
}
