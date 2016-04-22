package eu.codlab.amiiwrite.amiitool;

import android.content.Context;
import android.util.Log;

import eu.codlab.amiitool.AmiiTool;
import eu.codlab.amiiwrite.database.models.Amiibo;

/**
 * Created by kevinleperf on 22/04/16.
 */
public class AmiitoolFactory {
    private static AmiitoolFactory _sInstance = new AmiitoolFactory();

    private AmiitoolFactory() {

    }

    public static AmiitoolFactory getInstance() {
        return _sInstance;
    }

    private AmiiTool mAmiitool;

    public void init(Context context) {
        mAmiitool = new AmiiTool(context);
    }

    public byte[] unpack(Amiibo amiibo) {
        byte[] encrypted = amiibo.data.getBlob();
        return unpack(encrypted);
    }

    public byte[] unpack(byte[] encrypted) {
        byte[] decrypted = new byte[encrypted.length];
        mAmiitool.unpack(encrypted, encrypted.length, decrypted, decrypted.length);

        Log.d("AmiitoolFactory", "encrypted := " + byteArrayToHex(encrypted));
        Log.d("AmiitoolFactory", "decrypted := " + byteArrayToHex(decrypted));
        return decrypted;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("\\x%02x", b & 0xff));
        return sb.toString();
    }

}
