package eu.codlab.amiiwrite;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import eu.codlab.amiiwrite.amiibo.AmiiboHelper;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class ApplicationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(this);

        AmiiboHelper.init();
    }
}
