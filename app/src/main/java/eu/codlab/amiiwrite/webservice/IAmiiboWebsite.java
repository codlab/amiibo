package eu.codlab.amiiwrite.webservice;

import com.squareup.okhttp.Callback;

import eu.codlab.amiiwrite.webservice.models.WebsiteInformation;
import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public interface IAmiiboWebsite {

    @GET("/amiibos")
    Call<WebsiteInformation> retrieveInformations();
}
