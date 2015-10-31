package eu.codlab.amiiwrite.amiibo;

import android.nfc.TagLostException;
import android.nfc.tech.NfcA;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.util.Arrays;

import eu.codlab.amiiwrite.utils.IO;

/**
 * Created by kevinleperf on 29/10/2015.
 */
public class AmiiboIO {
    public static boolean authenticateAmiibo(NfcA tag, byte[] uid) {
        byte[] password = AmiiboMethods.keygen(uid);

        byte[] auth = new byte[]{
                (byte) 0x1B,
                password[0],
                password[1],
                password[2],
                password[3]
        };
        Log.d("MainActivity", "having auth " + IO.byteArrayToLoggableHexString(auth));
        byte[] response = new byte[0];
        try {
            response = tag.transceive(auth);
            Log.d("MainActivity", "having response " + IO.byteArrayToLoggableHexString(response));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readAmiibo(NfcA tag) {
        byte[] obtained = null;

        SparseArray<byte[]> read_blocks = new SparseArray<>();


        try {
            for (int i = 0; i < Constants.MAX_READABLE_PAGES; i += 4) {
                byte[] write = new byte[]{Constants.COMMAND_READ, (byte) i};
                byte[] response = null;
                Log.d("MainActivity", "read I :: " + IO.byteArrayToLoggableHexString(write));
                boolean continue_with_except = true;
                try {
                    response = tag.transceive(write);
                } catch (TagLostException e) {
                    response = null;
                    continue_with_except = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    response = null;
                }

                if (response != null && response.length >= 16) {
                    //we always retrieve 4pages
                    byte[] first = Arrays.copyOfRange(response, 0, 4);
                    byte[] second = Arrays.copyOfRange(response, 4, 8);
                    byte[] third = Arrays.copyOfRange(response, 8, 12);
                    byte[] fourth = Arrays.copyOfRange(response, 12, 16);
                    read_blocks.put(i, first);
                    read_blocks.put(i + 1, second);
                    read_blocks.put(i + 2, third);
                    read_blocks.put(i + 3, fourth);
                } else if (!continue_with_except) {
                    throw new TagLostException("having lost completely the tag");
                }
            }
        } catch (TagLostException e) {
            e.printStackTrace();
            read_blocks.clear();
            return null;
        }


        //construct the final array
        if (read_blocks.size() > 0) {
            obtained = new byte[4 * Constants.MAX_READABLE_PAGES];

            for (int i = 0; i <= Constants.LAST_READABLE_PAGE; i++) {
                int delta = i * 4;
                byte[] page = read_blocks.get(i);
                for (int j = 0; j < 4; j++) obtained[delta + j] = page[j];
            }
        }

        return obtained;
    }

    public static void writeAmiibo(NfcA tag, byte[] bytes) {
        for (String string : tag.getTag().getTechList())
            Log.d("MainActivity", "technos " + string);

        SparseArray<byte[]> pages = new SparseArray<>();
        AmiiboHelper.appendPagesFromBytes(pages, bytes);


        try {

            /*
            Not possible since those configuration pages are write locked
            setProt(tag, false, 0);
            setAuth0(tag, 0xff);
            setLock(tag, false, false, false, false, false, false, false, false);*/
            int error = 0;

            for (int key_index = 0; key_index < pages.size(); key_index++) {
                int page_index = pages.keyAt(key_index);
                byte[] page = pages.get(page_index);
                try {
                    Log.d("MainActivity", "writeNdef pages[I]" + page_index + " :: " + IO.byteArrayToLoggableHexString(page));

                    byte[] write = tag.transceive(new byte[]{
                            (byte) Constants.COMMAND_WRITE, // COMMAND_WRITE
                            (byte) (page_index & 0xff),
                            page[0],
                            page[1],
                            page[2],
                            page[3],
                    });
                    Log.d("MainActivity", "writeNdef pages[O]" + page_index + " :: " + IO.byteArrayToLoggableHexString(write));
                    error = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("", "");
                    //if we have less then 3 errors OR we already "outreached" the authentication issue
                    if ((page_index > 5) || error < 3) error++;
                    else throw new Exception("oops");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
