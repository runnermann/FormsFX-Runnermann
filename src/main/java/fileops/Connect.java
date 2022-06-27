package fileops;

public enum Connect {
      LINK {
            //      public String getLink() {
//            System.err.println("\n\n\n-------------------------------------------------------------------------------------");
//            System.err.println("\n\n\n\t\t\tWARNING: USING LOCAL HOST");
//            System.err.println("\t\t\tWARNING: USING LOCAL HOST");
//            System.err.println("\t\t\tWARNING: USING LOCAL HOST\n\n\n");
//            System.err.println("\n\n\t\t\tChange Connect.getLink to \"return remote\" \n\n");
//            System.err.println("-------------------------------------------------------------------------------------\n\n\n");
//            // !!! DO NOT CHANGE. Uncomment lower method !!!
// //           return local;
//            return ngrokTesting;
//        }
            public String getLink() {
                  return remote;
            }
      };

      // --------------------------------- --------------------------------- //
      //                               Common
      // --------------------------------- --------------------------------- //
      //private static final String local = "http://localhost:8080";
      //private static final String ngrokTesting = "https://0766b27a8822.ngrok.io";
      private static final String remote = "https://www.flashmonkey.xyz";

      Connect() { /* empty constructor */ }

      public abstract String getLink();
}
