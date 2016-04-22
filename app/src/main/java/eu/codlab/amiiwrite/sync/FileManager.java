package eu.codlab.amiiwrite.sync;

import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import eu.codlab.amiiwrite.database.models.Amiibo;
import hugo.weaving.DebugLog;

/**
 * Created by kevinleperf on 02/09/15.
 */
class FileManager {
    private final static String AMIIBO_FOLDER = "amiibos";
    private final static String EXTENSION = ".amiibo";
    private final static String FILE_TPL = "amiibo_%s" + EXTENSION;
    private final static String MIMETYPE = "text/plain";
    private SyncService _parent;

    DriveFolder app_folder_for_user = null;

    private FileManager() {

    }

    public FileManager(SyncService parent) {
        _parent = parent;
    }

    @DebugLog
    public void init(SyncService parent) {
        _parent = parent;
    }

    public void onDestroy() {
        _parent = null;
    }

    @DebugLog
    private boolean hasFileLocally(String file_name) {
        String[] name = file_name.split(EXTENSION);
        name = name[0].split("_");
        if (name.length != 2) return true;//return true to avoid this
        return _parent.exists(name[1]);
    }

    @DebugLog
    public void listFiles() {

        if (app_folder_for_user != null) {

            Log.d("FileManager", app_folder_for_user.getDriveId().toString());

            app_folder_for_user.listChildren(_parent.getClient())
                    .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                        @Override
                        public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                            List<Metadata> files = new ArrayList<>();
                            MetadataBuffer buffer = metadataBufferResult.getMetadataBuffer();
                            Metadata data;
                            Log.d("FileManager", "onResult " + buffer.getCount() + " " + metadataBufferResult.getStatus().isSuccess());

                            for (int i = 0; i < buffer.getCount(); i++) {
                                data = buffer.get(i);
                                Log.d("FileManager", "data :: " + data.getTitle());
                                if (data.getTitle().endsWith(EXTENSION)) {
                                    if (!hasFileLocally(data.getTitle())) {
                                        files.add(data);
                                    }
                                }
                            }

                            _parent.onListFilesResult(files);
                        }
                    });
        } else {
            getAmiiboFolder();
        }
    }

    @DebugLog
    private void getAmiiboFolder() {
        Drive.DriveApi.getRootFolder(_parent.getClient())
                .listChildren(_parent.getClient())
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                        boolean found = false;
                        try {
                            List<Metadata> files = new ArrayList<>();
                            MetadataBuffer buffer = metadataBufferResult.getMetadataBuffer();
                            Metadata data;

                            for (int i = 0; i < buffer.getCount(); i++) {
                                data = buffer.get(i);
                                if (data.getTitle().equals(AMIIBO_FOLDER) && data.isFolder()
                                        && !data.isTrashed()) {
                                    app_folder_for_user = Drive.DriveApi
                                            .getFolder(_parent.getClient(), data.getDriveId());
                                    Log.d("FileManager", data.getDriveId() + " " + data.getTitle());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (app_folder_for_user != null) {
                            listFiles();
                        } else {
                            createAmiiboFolder();
                        }
                    }
                });
    }

    @DebugLog
    private void createAmiiboFolder() {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(AMIIBO_FOLDER).build();

        Drive.DriveApi.getRootFolder(_parent.getClient())
                .createFolder(_parent.getClient(), changeSet)
                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                        if (driveFolderResult.getStatus().isSuccess()) {
                            app_folder_for_user = driveFolderResult.getDriveFolder();
                            listFiles();
                        } else {
                            app_folder_for_user = null;
                            _parent.onPushFinished();
                        }
                    }
                });
    }

    @DebugLog
    public void pushToDrive(List<Amiibo> amiibos) {
        if (amiibos.size() > 0) {
            Amiibo amiibo = amiibos.get(0);
            amiibos.remove(0);
            pushToDrive(amiibo, amiibos);
        } else {
            app_folder_for_user = null;
            _parent.onPushFinished();
        }
    }

    @DebugLog
    public void pushToDrive(final Amiibo amiibo, final List<Amiibo> amiibos) {
        Drive.DriveApi.newDriveContents(_parent.getClient())
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        AmiiboFile to_write = AmiiboFile.fromAmiibo(amiibo);
                        if (result.getStatus().isSuccess()) {
                            OutputStream fileOutputStream = result.getDriveContents().getOutputStream();
                            Writer writer = new OutputStreamWriter(fileOutputStream);
                            try {
                                writer.write(to_write.toString());
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(String.format(FILE_TPL, to_write.uuid))
                                    .setMimeType(MIMETYPE).build();

                            // Create a file in the root folder
                            app_folder_for_user
                                    .createFile(_parent.getClient(), changeSet, result.getDriveContents())
                                    .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                        @Override
                                        public void onResult(DriveFolder.DriveFileResult result) {
                                            boolean success = result.getStatus().isSuccess();
                                            if (success) {
                                                _parent.setUploaded(amiibo.uuid, true);
                                            }
                                            _parent.onPushResult(amiibo, success);
                                            pushToDrive(amiibos);
                                        }
                                    });
                        } else {
                            _parent.onPushResult(amiibo, false);
                            pushToDrive(amiibos);
                        }
                    }
                });
    }

    @DebugLog
    public void readFileContent(List<Metadata> remainings) {
        if (remainings.size() > 0) {
            Metadata metadata = remainings.get(0);
            remainings.remove(0);
            readFileContent(metadata.getTitle(), remainings);
        } else {
            _parent.onReadFinished();
        }
    }

    @DebugLog
    public void readFileContent(final String file_name, final List<Metadata> remainings) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, file_name))
                .build();

        app_folder_for_user.queryChildren(_parent.getClient(), query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                        MetadataBuffer buffer = metadataBufferResult.getMetadataBuffer();
                        int count = buffer.getCount();
                        if (count > 0) {
                            readFileFromDrive(buffer, buffer.get(0).getDriveId());
                        } else {
                            _parent.onFileRead(null);
                        }

                        readFileContent(remainings);
                    }
                });
    }

    @DebugLog
    private void readFileFromDrive(final MetadataBuffer meta_data, final DriveId drive_id) {
        Drive.DriveApi.getFile(_parent.getClient(), drive_id)
                .open(_parent.getClient(), DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        try {
                            InputStream input = driveContentsResult.getDriveContents()
                                    .getInputStream();
                            String total = consumeStream(input);

                            AmiiboFile amiibo_file = AmiiboFile.fromString(total.toString());
                            _parent.onFileRead(amiibo_file);
                            if (!meta_data.isClosed()) meta_data.release();
                        } catch (IOException e) {
                            e.printStackTrace();
                            _parent.onFileRead(null);
                        }
                    }
                });
    }

    @DebugLog
    private String consumeStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            total.append(line);
        }
        try {
            reader.close();
            stream.close();
        } catch (Exception e) {

        }
        return total.toString();
    }
}
