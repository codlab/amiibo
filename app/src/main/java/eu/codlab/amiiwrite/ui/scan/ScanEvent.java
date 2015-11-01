package eu.codlab.amiiwrite.ui.scan;

/**
 * Created by kevinleperf on 31/10/2015.
 */
public class ScanEvent {
    private ScanEvent() {

    }

    public static class StartFragment {

    }

    public static class AmiiboToSave {
        public byte[] data;

        public AmiiboToSave(byte[] data) {
            this.data = data;
        }
    }

    public static class StartWriteFragment {
        public long id;

        public StartWriteFragment(long id) {
            this.id = id;
        }
    }
}
