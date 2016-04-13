package eu.codlab.amiiwrite.sync;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Metadata;

import java.util.List;

import de.greenrobot.event.EventBus;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.events.SyncResult;
import hugo.weaving.DebugLog;

/**
 * Created by kevinleperf on 24/08/15.
 */
public class SyncService extends Service {
    private State _state = State.FINISHED;

    private enum State {
        STARTED,
        FINISHED
    }

    private static SyncService _instance;

    public static void start(Activity parent) {
        Intent service = new Intent(parent, SyncService.class);
        parent.startService(service);
    }

    public static SyncService getInstance() {
        return _instance;
    }

    private GoogleApiClient _google_api_client;
    private FileManager _file_manager;

    private boolean checkState() {
        return _google_api_client != null && _google_api_client.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
        _file_manager = new FileManager(this);
        onPushFinished();

        checkState();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public void onDestroy() {
        _instance = null;
        _file_manager.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new myBinder();

    @DebugLog
    public void setGoogleApiClient(GoogleApiClient google_api_client) {
        _google_api_client = google_api_client;

        checkState();
    }

    @DebugLog
    public void init() {
        _state = State.STARTED;
        _file_manager.init(this);
        _file_manager.listFiles();
        checkState();
    }

    @DebugLog
    public GoogleApiClient getClient() {
        return _google_api_client;
    }

    @DebugLog
    public boolean exists(final String uuid) {
        Amiibo amiibo = AmiiboController.getInstance().getAmiibo(uuid);
        return amiibo != null;
    }

    /**
     * Call the retrieval of files from google drive
     */
    @DebugLog
    public void listFiles() {
        _file_manager.listFiles();
    }

    /**
     * The list of files retrieved
     *
     * @param files the list of files metadata, not null
     */
    @DebugLog
    public void onListFilesResult(List<Metadata> files) {
        _file_manager.readFileContent(files);
    }

    /**
     * Called when the lists of files are fully retrieved
     */
    @DebugLog
    public void onReadFinished() {
        upload();
    }

    /**
     * Upload the locally unsynced files
     */
    @DebugLog
    public void upload() {
        List<Amiibo> amiibos = AmiiboController.getInstance()
                .getAmiibosUnsynced();

        _file_manager.pushToDrive(amiibos);
    }

    /**
     * When the files are all synced upward
     */
    @DebugLog
    public void onPushFinished() {
        _state = State.FINISHED;
        EventBus.getDefault().postSticky(new SyncResult(true));
    }

    @DebugLog
    public void onFileRead(AmiiboFile result) {
        CustomEvent event = new CustomEvent("Download");
        event.putCustomAttribute("success", result != null ? "true" : "false");
        Answers.getInstance().logCustom(event);

        if (result != null) {
            Amiibo amiibo = AmiiboController.getInstance().getAmiibo(result.uuid);
            Amiibo to_write = result.toAmiibo();
            if (amiibo != null) {
                to_write.id = amiibo.id;
            }
            to_write.synced = true;
            AmiiboController.getInstance().updateAmiibo(to_write);
        }
    }

    @DebugLog
    public void onPushResult(Amiibo amiibo, boolean success) {
        CustomEvent event = new CustomEvent("Upload");
        event.putCustomAttribute("success", success ? "true" : "false");
        Answers.getInstance().logCustom(event);
    }

    public void setUploaded(String uuid, boolean success) {
        AmiiboController.getInstance().setUploaded(uuid, success);
    }

    public class myBinder extends Binder {
        public SyncService getService() {
            return SyncService.this;
        }
    }

    public boolean isFinished() {
        return _state.ordinal() == State.FINISHED.ordinal();
    }
}
