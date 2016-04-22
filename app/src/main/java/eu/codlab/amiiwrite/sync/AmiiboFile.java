package eu.codlab.amiiwrite.sync;

import android.util.Base64;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import java.util.UUID;

import eu.codlab.amiiwrite.database.models.Amiibo;

/**
 * Created by kevinleperf on 07/11/2015.
 */
public class AmiiboFile {
    public String name;

    public String uuid;

    public String game_name;

    public long created_at;

    public String amiibo_identifier;

    public String data;

    public boolean synced;

    public Amiibo toAmiibo() {
        try {
            Amiibo amiibo = new Amiibo();
            amiibo.name = name;
            amiibo.uuid = uuid;
            if (amiibo.uuid == null) amiibo.uuid = UUID.randomUUID().toString();
            amiibo.game_name = game_name;
            amiibo.created_at = created_at;
            amiibo.data = new Blob(Base64.decode(data, Base64.URL_SAFE | Base64.NO_WRAP));
            amiibo.amiibo_identifier = amiibo_identifier;

            return amiibo;
        } catch (Exception exception_while_parsing) {
            exception_while_parsing.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public static AmiiboFile fromString(String jsonobject) {
        return new Gson().fromJson(jsonobject, AmiiboFile.class);
    }

    public static AmiiboFile fromAmiibo(Amiibo amiibo) {
        AmiiboFile file = new AmiiboFile();
        file.name = amiibo.name;
        file.uuid = amiibo.uuid;
        file.game_name = amiibo.game_name;
        file.created_at = amiibo.created_at;
        file.data = Base64.encodeToString(amiibo.data.getBlob(), Base64.URL_SAFE | Base64.NO_WRAP);
        return file;
    }
}
