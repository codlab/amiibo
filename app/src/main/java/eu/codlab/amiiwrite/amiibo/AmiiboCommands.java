package eu.codlab.amiiwrite.amiibo;

import android.nfc.tech.NfcA;

import java.io.IOException;

/**
 * Those methods were here to try editing the specific ntag215 blocks
 * <p/>
 * unfortunately, impossible but still here for knowledge purpose
 * <p/>
 * <p/>
 * Created by kevinleperf on 30/10/2015.
 */
public class AmiiboCommands {

    private void setLock(NfcA tag, boolean lock_128_129,
                         boolean lock_112_127,
                         boolean lock_96_111,
                         boolean lock_80_95,
                         boolean lock_64_79,
                         boolean lock_48_63,
                         boolean lock_32_47,
                         boolean lock_16_31) {
        byte[] response = new byte[0];
        try {
            response = tag.transceive(new byte[]{
                    (byte) Constants.COMMAND_READ, // COMMAND_READ
                    (byte) 0x82    // page address
            });
            byte lock = (byte) ((lock_128_129 ? 1 << 7 : 0)
                    + (lock_112_127 ? 1 << 6 : 0)
                    + (lock_96_111 ? 1 << 5 : 0)
                    + (lock_80_95 ? 1 << 4 : 0)
                    + (lock_64_79 ? 1 << 3 : 0)
                    + (lock_48_63 ? 1 << 2 : 0)
                    + (lock_32_47 ? 1 << 1 : 0)
                    + (lock_16_31 ? 1 : 0));
            if ((response != null)) {  // read always returns 4 pages
                byte[] write = new byte[]{
                        (byte) 0xA2, // COMMAND_WRITE
                        (byte) 38,   // page address
                        lock,
                        response[1], response[2], response[3]  // keep old value for bytes 1-3, you could also simply set them to 0 as they are currently RFU and must always be written as 0 (response[1], response[2], response[3] will contain 0 too as they contain the read RFU value)
                };
                response = tag.transceive(write);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProt(NfcA tag, boolean prot, int authlim) {
        byte[] response = new byte[0];
        try {
            response = tag.transceive(new byte[]{
                    (byte) Constants.COMMAND_READ, // COMMAND_READ
                    (byte) 0x84    // page address
            });
            if ((response != null)) {  // read always returns 4 pages
                byte[] write = new byte[]{
                        (byte) 0xA2, // COMMAND_WRITE
                        (byte) 38,   // page address
                        (byte) ((response[0] & 0x078) | (prot ? 0x080 : 0x000) | (authlim & 0x007)),
                        response[1], response[2], response[3]  // keep old value for bytes 1-3, you could also simply set them to 0 as they are currently RFU and must always be written as 0 (response[1], response[2], response[3] will contain 0 too as they contain the read RFU value)
                };
                response = tag.transceive(write);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // first page to be protected, set to a value between 0 and 37 for NTAG212
    private void setAuth0(NfcA tag, int auth0) {
        byte[] response = new byte[0];
        try {
            response = tag.transceive(new byte[]{
                    (byte) Constants.COMMAND_READ, // COMMAND_READ
                    (byte) 0x83    // page address
            });
            if ((response != null) && (response.length >= 16)) {  // read always returns 4 pages
                byte[] write = new byte[]{
                        (byte) 0xA2, // COMMAND_WRITE
                        (byte) 37,   // page address
                        response[0], // keep old value for byte 0
                        response[1], // keep old value for byte 1
                        response[2], // keep old value for byte 2
                        (byte) (auth0 & 0x0ff)
                };
                response = tag.transceive(write);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
