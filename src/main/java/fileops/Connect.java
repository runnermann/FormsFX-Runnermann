package fileops;

public enum Connect {
    LINK {
        public String getLink() {
            return local;
        }
    };

    // --------------------------------- --------------------------------- //
    //                               Common
    // --------------------------------- --------------------------------- //
    private static final String local = "http://localhost:8080";
    private static final String remote = "https://www.flashmonkey.xyz";

    Connect() { /* empty constructor */ }

    public abstract String getLink();
}
