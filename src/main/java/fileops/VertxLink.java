package fileops;

public enum VertxLink {
    // request for creating a member account
    CANCELLED() {
        final String endpoint = "/cancel";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    CANCEL_POLICY() {
        final String endpoint = "/earning-money-as-a-college-student";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    DECK_DESCRIPT_PHOTO() {
        final String endpoint = "https://flashmonkey-deck-photo.s3.us-west-2.amazonaws.com/";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return endpoint; }
    },
    // calls for a deck...
    GET_RESOURCE() {
        final String endpoint = "/P01/A77J30";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    // hyperlink address
    ONBOARD() {
        final String endpoint = "/P01/F06H21";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    QRCODE_DECK() {
        final String endpoint = "/Q52/FFG415/:";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    QRCODE_PREDCHAIN() {
        final String endpoint = "/Q52/A017BA/:";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    REQ_ACCT() {
        final String endpoint = "/req-acct";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    REQ_MEMBER() {
        final String endpoint = "/P01/HF25XZ";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    REQ_PURCHASE() {
        final String endpoint = "/P01/F7G4l5";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    EULA_POLICY() {
        final String endpoint = "/eula-agreement";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    SUCCESS() {
        final String endpoint = "/P01/A309FF";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    SUBSCRIPT_CANCEL() {
        final String endpoint = "/P01/CA25XZ";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    },
    USER_AVATAR() {
        final String endpoint = "https://flashmonkey-avatar.s3.us-west-2.amazonaws.com/";
        public String getEndPoint() { return endpoint; }
        // Stored in S3 thus getLink is same as endpoint.
        public String getLink() { return endpoint; }
    },
    USER_PAGE() {
        final String endpoint = "/Q52/UPG022/:";
        public String getEndPoint() { return endpoint; }
        public String getLink() { return DOMAIN + endpoint; }
    };


    // --------------------------------- --------------------------------- //
    //                             COMMON
    // --------------------------------- --------------------------------- //

    // Always points towards the current domain no matter the mode. IE
    // testing with localhost.
    private static final String DOMAIN = Connect.LINK.getLink();
    // Some endpoints will always reference vertx dispite if we
    // are in test mode.
    // private static final String XYZ = "https://www.flashmonkey.co";

    VertxLink() { /* empty constructor */ }

    public abstract String getLink();

    /**
     * Does not include the website.
     * @return Returns the file extension for the endPoint. I.e. /IDK/server-endpoint
     */
    public abstract String getEndPoint();

}
