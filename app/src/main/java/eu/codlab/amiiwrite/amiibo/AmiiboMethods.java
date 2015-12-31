package eu.codlab.amiiwrite.amiibo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by kevinleperf on 30/10/2015.
 */
public class AmiiboMethods {
    private static byte[] ERROR = new byte[]{0};

    public static boolean isError(@Nullable byte[] check) {
        return check == null || Arrays.equals(check, ERROR);
    }

    @NonNull
    public static byte[] keygen(@NonNull byte[] uuid) {
        byte[] key = new byte[4];
        int[] uuid_to_ints = new int[uuid.length];

        for (int i = 0; i < uuid.length; i++)
            uuid_to_ints[i] = (0xFF & uuid[i]);

        if (uuid.length == 7) {
            key[0] = ((byte) (0xFF & (0xAA ^ (uuid_to_ints[1] ^ uuid_to_ints[3]))));
            key[1] = ((byte) (0xFF & (0x55 ^ (uuid_to_ints[2] ^ uuid_to_ints[4]))));
            key[2] = ((byte) (0xFF & (0xAA ^ (uuid_to_ints[3] ^ uuid_to_ints[5]))));
            key[3] = ((byte) (0xFF & (0x55 ^ (uuid_to_ints[4] ^ uuid_to_ints[6]))));
            return key;
        }

        return ERROR;
    }

    @NonNull
    public static byte[] amiiboIdentifier(@Nullable byte[] read_data) {
        if (read_data == null) return ERROR;
        return AmiiboHelper.getPage(read_data, Constants.AMIIBO_IDENTIFIER_PAGE_1, 2);
    }

    public static int getAmiiboDrawable(@NonNull Context context,
                                        @NonNull String amiibo_identifier) {
        String output = String.format("icon_%s", amiibo_identifier.toLowerCase());
        return context.getResources().getIdentifier(output, "drawable", context.getPackageName());
    }
}
