package eu.codlab.amiiwrite.webservice;

import eu.codlab.amiiwrite.webservice.models.WebsiteInformation;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * This class performs the webservice instantiations
 * <p/>
 * Created by kevinleperf on 01/11/2015.
 */
public class AmiiboWebsiteController {
    private static final String AMIIBO_WEBSITE = "http://amiibo.codlab.eu";
    private Retrofit _adapter;
    private IAmiiboWebsite _webservice;

    public AmiiboWebsiteController() {
        init();
    }

    /**
     * Init the proxy webservice controller
     * <p/>
     * Keep instance of the retrofit instantiation and the webservice caller
     */
    public void init() {
        _adapter = new Retrofit.Builder()
                .baseUrl(AMIIBO_WEBSITE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _webservice = _adapter.create(IAmiiboWebsite.class);
    }

    /**
     * Retrieve every important information
     * <p/>
     * - the current Amiibo revision
     * - the current list of amiibos
     * - the online apk version and url, to manage new version
     *
     * @return a callable object to be used as callback
     */
    public Call<WebsiteInformation> retrieveInformation() {
        return _webservice.retrieveInformations();
    }
}
