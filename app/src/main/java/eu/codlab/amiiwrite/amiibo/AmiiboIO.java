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
        byte[] response = new byte[0];
        try {
            response = tag.transceive(auth);
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

    public static boolean writeAmiibo(NfcA tag, byte[] bytes) {
        for (String string : tag.getTag().getTechList())
            Log.d("MainActivity", "technos " + string);

        SparseArray<byte[]> pages = new SparseArray<>();
        AmiiboHelper.appendPagesFromBytes(pages, bytes);


        try {
            int error = 0;

            for (int key_index = 0; key_index < pages.size(); key_index++) {
                boolean continue_with_except = true;

                int page_index = pages.keyAt(key_index);
                byte[] page = pages.get(page_index);
                byte[] response = null;

                byte[] write = new byte[]{
                        Constants.COMMAND_WRITE, // COMMAND_WRITE
                        (byte) (page_index & 0xff),
                        page[0],
                        page[1],
                        page[2],
                        page[3],
                };
                try {
                    response = tag.transceive(write);
                    Log.d("MainActivity", "write O :: " + IO.byteArrayToLoggableHexString(write) + " OK");
                } catch (TagLostException e) {
                    response = null;
                    continue_with_except = false;
                } catch (IOException e) {
                    Log.d("MainActivity", "write O :: " + IO.byteArrayToLoggableHexString(write) + " KO");
                    response = null;
                }

                if (response != null) {

                } else if (!continue_with_except) {
                    return false;
                    //throw new TagLostException("having lost completely the tag");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
