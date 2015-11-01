package eu.codlab.amiiwrite.webservice;

import eu.codlab.amiiwrite.webservice.models.WebsiteInformation;
import retrofit.Call;
import retrofit.http.GET;

/**
 * Describe where are the relevant information for the webservice
 * <p/>
 * Created by kevinleperf on 01/11/2015.
 */
public interface IAmiiboWebsite {

    @GET("/amiibos")
    Call<WebsiteInformation> retrieveInformations();
}
