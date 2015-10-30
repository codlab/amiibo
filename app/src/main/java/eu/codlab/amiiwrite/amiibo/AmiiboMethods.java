package eu.codlab.amiiwrite.amiibo;

/**
 * Created by kevinleperf on 30/10/2015.
 */
public class AmiiboMethods {

    public static byte[] keygen(byte[] uuid) {
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

        return null;
    }

    public static byte[] amiiboIdentifier(byte[] read_data) {
        if (read_data == null) return new byte[]{0};
        return AmiiboHelper.getPage(read_data, Constants.AMIIBO_IDENTIFIER_PAGE_1, 2);
    }
}
