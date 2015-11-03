package eu.codlab.amiiwrite.webservice.models;

import eu.codlab.amiiwrite.database.models.AmiiboDescriptor;

/**
 * Describe a simple amiibo information obtained from the server
 * <p/>
 * Created by kevinleperf on 01/11/2015.
 */
public class AmiiboDescriptorInformation {
    /**
     * The human-readable name of the amiibo
     */
    public String name;

    /**
     * The unique amiibo identifier obtained from there page 22/23
     */
    public String identifier;

    public AmiiboDescriptor asAmiiboDescriptor() {
        AmiiboDescriptor descriptor = new AmiiboDescriptor();
        descriptor.name = name;
        descriptor.amiibo_identifier = identifier;
        return descriptor;
    }
}
