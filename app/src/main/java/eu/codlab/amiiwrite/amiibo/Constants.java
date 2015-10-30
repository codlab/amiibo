package eu.codlab.amiiwrite.amiibo;

/**
 * Created by kevinleperf on 30/10/2015.
 */
public class Constants {
    public static final int LAST_READABLE_PAGE = 129;
    public static final int MAX_READABLE_PAGES = 130;
    public static final byte COMMAND_READ = 0x30;
    public static final byte COMMAND_WRITE = (byte) 0xA2;
    public static final int AMIIBO_IDENTIFIER_PAGE_1 = 21;
    public static final int AMIIBO_IDENTIFIER_PAGE_2 = 21;
}
