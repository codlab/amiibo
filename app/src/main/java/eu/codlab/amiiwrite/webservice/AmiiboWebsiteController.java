package eu.codlab.amiiwrite.webservice;

import eu.codlab.amiiwrite.webservice.models.WebsiteInformation;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class AmiiboWebsiteController {
    private static final String AMIIBO_WEBSITE = "http://amiibo.codlab.eu";
    private Retrofit _adapter;
    private IAmiiboWebsite _webservice;

    public AmiiboWebsiteController() {
        init();
    }

    public void init() {
        _adapter = new Retrofit.Builder()
                .baseUrl(AMIIBO_WEBSITE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _webservice = _adapter.create(IAmiiboWebsite.class);
    }

    public Call<WebsiteInformation> retrieveInformation() {
        return _webservice.retrieveInformations();
    }
}
