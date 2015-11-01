package eu.codlab.amiiwrite;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import eu.codlab.amiiwrite.amiibo.AmiiboHelper;

/**
 * Overall application controller. Is instantiate before any other UI item
 * <p/>
 * <p/>
 * Created by kevinleperf on 31/10/2015.
 */
public class ApplicationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //init the database orm
        FlowManager.init(this);

        //init the AmiiboHelper
        AmiiboHelper.init();
    }
}
