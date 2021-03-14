package ecosystem;

import javafx.scene.control.Hyperlink;

public enum VertxLink {
    // hyperlink address
    ONBOARD() {
        public String getLink() {
            return connect + "/onboard";
        }
    },
    // is anyone using this??? calls for a deck...
    GET_RESOURCE() {
        public String getLink() {
            return connect + "/resource";
        }
    },
    REQ_PURCHASE() {
        public String getLink() {
            return connect + "/req-purchase";
        }
    },
    CANCELLED() {
        public String getLink() {
            return connect + "/cancel";
        }
    },
    SUCCESS() {
        public String getLink() {
            return connect + "/success";
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
