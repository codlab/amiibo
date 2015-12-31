package eu.codlab.amiiwrite;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import eu.codlab.amiiwrite.amiibo.AmiiboHelper;
import eu.codlab.amiiwrite.database.controllers.AmiiboFactory;
import eu.codlab.amiiwrite.events.PostRefreshAmiibos;
import eu.codlab.amiiwrite.webservice.AmiiboWebsiteController;
import eu.codlab.amiiwrite.webservice.models.AmiiboDescriptorInformation;
import eu.codlab.amiiwrite.webservice.models.WebsiteInformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Overall application controller. Is instantiate before any other UI item
 * <p/>
 * <p/>
 * Created by kevinleperf on 31/10/2015.
 */
public class ApplicationController extends Application {
    private boolean _updated;
    private boolean _in_update;
    private AmiiboWebsiteController _website_controller;
    private EventBus mApplicationBus;

    private AmiiboWebsiteController getAmiiboWebsiteController() {
        if (_website_controller == null) _website_controller = new AmiiboWebsiteController();
        return _website_controller;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _updated = false;
        _in_update = false;

        //init the database orm
        FlowManager.init(this);

        //init the AmiiboHelper
        AmiiboHelper.init();

        mApplicationBus = EventBus.builder().build();

        mApplicationBus.register(this);
    }

    @Override
    public void onTerminate() {
        mApplicationBus.unregister(this);
        super.onTerminate();
    }

    public void tryUpdateFromNetwork(final Activity activity) {
        if (_in_update || _updated) return;

        //prevent back into the method -- not threadsafe for 2 instr, but nvm
        _in_update = true;

        Call<WebsiteInformation> information = getAmiiboWebsiteController().retrieveInformation();
        information.enqueue(new Callback<WebsiteInformation>() {
            @Override
            public void onResponse(Response<WebsiteInformation> response, Retrofit retrofit) {
                Log.d("MainActivity", "onResponse");
                if (response != null) {
                    final WebsiteInformation information = response.body();
                    Log.d("MainActivity", "onResponse " + information);
                    if (BuildConfig.VERSION_CODE < information.apk.version) {
                        new MaterialDialog.Builder(activity)
                                .title(R.string.new_version_title)
                                .content(R.string.new_version_content)
                                .positiveText(android.R.string.ok)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(information.apk.url));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                .show();

                    }
                    _in_update = false;
                    _updated = true;

                    mApplicationBus.post(new PostRefreshAmiibos(information.amiibos));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.d("MainActivity", "onResponse " + t);
                _in_update = false;
                _updated = false;
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.Async)
    public void onEvent(PostRefreshAmiibos event) {
        List<AmiiboDescriptorInformation> list = event.amiibos;
        if (list != null) {
            for (AmiiboDescriptorInformation amiibo : list) {
                Log.d("ApplicationController", "writing amiibo " + amiibo.identifier + " " + amiibo.name);
                AmiiboFactory.getAmiiboDescriptorCache()
                        .updateInDatabase(amiibo.asAmiiboDescriptor());
            }
        }
    }
}
