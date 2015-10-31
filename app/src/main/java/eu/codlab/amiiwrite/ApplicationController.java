package eu.codlab.amiiwrite;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class ApplicationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(this);
    }
}
