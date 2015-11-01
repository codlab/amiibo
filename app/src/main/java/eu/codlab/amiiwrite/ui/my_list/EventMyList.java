package eu.codlab.amiiwrite.ui.my_list;

/**
 * Created by kevinleperf on 01/11/2015.
 */
public class EventMyList {

    private EventMyList() {

    }

    public static class EventLoadCategories {

    }

    public static class EventLoadAmiibos {
        public String identifier;

        public EventLoadAmiibos(String identifier) {
            this.identifier = identifier;
        }
    }

    public static class EventLoadAmiibo {
        public long id;

        public EventLoadAmiibo(long id) {
            this.id = id;
        }
    }
}
