package eu.codlab.amiiwrite;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import eu.codlab.amiiwrite.amiibo.AmiiboIO;
import eu.codlab.amiiwrite.amiibo.AmiiboMethods;
import eu.codlab.amiiwrite.utils.IO;

public class MainActivity extends AppCompatActivity {
    private String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(),
                    Ndef.class.getName(),
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent localPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(PendingIntent.FLAG_NO_CREATE), 0);
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        localIntentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        localIntentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, localPendingIntent, new IntentFilter[]{localIntentFilter}, this.techList);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onNewIntent(Intent paramIntent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(paramIntent.getAction())) {
            Tag tag = paramIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] uid = paramIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID);


            NfcA ntag215 = NfcA.get(tag);
            try {
                ntag215.connect();

                boolean authenticated = AmiiboIO.authenticateAmiibo(ntag215, uid);

                if (authenticated) {
                    byte[] read = AmiiboIO.readAmiibo(ntag215);
                    Log.d("MainActivity", "read amiibo " + IO.ByteArrayToHexString(AmiiboMethods.amiiboIdentifier(read)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ntag215.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
