package ecosystem;

import fileops.Connect;

public enum VertxLink {

    REQ_MEMBER() {
        public String getLink() {
            return connect + "/req-membership";
        }
    },
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
    },
    REQ_ACCT() {
        public String getLink() {
            return connect + "/req-acct";
        }
    };

    private static String connect = Connect.LINK.getLink();


    // --------------------------------- --------------------------------- //
    //                             CONSTRUCTORS
    // --------------------------------- --------------------------------- //
    VertxLink() { /* empty constructor */ }

    public abstract String getLink();

}
