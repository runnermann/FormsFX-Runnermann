package ecosystem;

import javafx.scene.control.Hyperlink;

public enum VertxLink {
    // hyperlink address
    ONBOARD() {
        public String getLink() {
            String link = "/onboard";
            return connect + link;
        }
    },
    // is anyone using this??? calls for a deck...
    GET_RESOURCE() {
        public String getLink() {
            String link = "/resource";
            return connect + link;
        }
    },
    REQ_PURCHASE() {
        public String getLink() {
            String link = "/req-purchase";
            return connect + link;
        }
    };

    private static String remote = "https://www.flashmonkey.xyz";
    private static String local  = "http://localhost:8080";
    private static String connect = local;


    // --------------------------------- --------------------------------- //
    //                             CONSTRUCTORS
    // --------------------------------- --------------------------------- //
    VertxLink() { /* empty constructor */ }

    public abstract String getLink();

}
