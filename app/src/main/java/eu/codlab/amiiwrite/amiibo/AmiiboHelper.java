package eu.codlab.amiiwrite.amiibo;

import android.util.SparseArray;

import java.util.Arrays;

/**
 * Created by kevinleperf on 29/10/2015.
 */
public class AmiiboHelper {

    /**
     * Store every REJECTED_PAGES_TO_WRITE
     * > unwritable pages and factory locked
     */
    private static final SparseArray<Boolean> REJECTED_PAGES_TO_WRITE = new SparseArray<>();

    {
        REJECTED_PAGES_TO_WRITE.put(0, true); //UID
        REJECTED_PAGES_TO_WRITE.put(1, true);
        REJECTED_PAGES_TO_WRITE.put(2, true);
        REJECTED_PAGES_TO_WRITE.put(3, true);
        for (int i = 0; i < 8; i++)
            REJECTED_PAGES_TO_WRITE.put(i + 0x0D, true); //13th page to the 31th include
        for (int i = 0; i < 0x0B; i++) REJECTED_PAGES_TO_WRITE.put(i + 0x15, true);
        REJECTED_PAGES_TO_WRITE.put(0x82, true);//dynamic lock bytes
        REJECTED_PAGES_TO_WRITE.put(0x83, true);//configuration
        REJECTED_PAGES_TO_WRITE.put(0x84, true);//configuration
        REJECTED_PAGES_TO_WRITE.put(0x85, true);//PWD
        REJECTED_PAGES_TO_WRITE.put(0x86, true);//PACK
    }


    /**
     * From a given data in intput, return the sub-byte array where the first returned
     * octet is the first octet of the given page with all the subsequents pages (limited to number)
     *
     * @param data
     * @param page
     * @param number_pages
     * @return
     */
    static byte[] getPage(byte[] data, int page, int number_pages) {
        int byte_read = 4 * number_pages;
        int start = page * 4;
        return Arrays.copyOfRange(data, start, start + byte_read);
    }

    /**
     * Appends a given page to the list of pages to write, also check for rejected pages
     *
     * @param pages_output
     * @param array_intput
     * @param page_number
     * @return
     */
    private static boolean appendPage(SparseArray<byte[]> pages_output,
                                      byte[] array_intput, int page_number) {
        return appendPage(pages_output, array_intput, page_number, false);
    }

    private static boolean appendPage(SparseArray<byte[]> pages_output,
                                      byte[] array_intput, int page_number,
                                      boolean include_rejected) {
        if (!include_rejected && REJECTED_PAGES_TO_WRITE.get(page_number) != null) return false;

        int i0 = page_number * 4;
        int i1 = i0 + 1;
        int i2 = i0 + 2;
        int i3 = i0 + 3;

        byte[] page = new byte[]{
                array_intput.length > i0 ? array_intput[i0] : 0,
                array_intput.length > i1 ? array_intput[i1] : 0,
                array_intput.length > i2 ? array_intput[i2] : 0,
                array_intput.length > i3 ? array_intput[i3] : 0,
        };

        pages_output.put(page_number, page);
        return true;
    }

    /**
     * From all the input data, return a complete list of pages to write
     *
     * @param pages_output
     * @param bytes_input
     * @return
     */
    public static boolean appendPagesFromBytes(SparseArray<byte[]> pages_output, byte[] bytes_input) {
        int block = 0;
        for (int i = 0; i < bytes_input.length; i += 4, block++) {
            appendPage(pages_output, bytes_input, block);
        }
        return true;
    }

    public static SparseArray pagesIntoList(byte[] bytes_input) {
        SparseArray<byte[]> pages_output = new SparseArray<>();
        for (int i = 0, block = 0; i < bytes_input.length; i += 4, block++) {
            appendPage(pages_output, bytes_input, block, true);
        }
        return pages_output;
    }


}
