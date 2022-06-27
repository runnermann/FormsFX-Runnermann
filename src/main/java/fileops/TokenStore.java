package fileops;

import fmexception.EmptyTokenException;

/**
 * Provides storage for tokens provided by Vertx.
 * Used by CloudOps.
 */
public enum TokenStore {

      INSTANCE;

      // Common fields and methods
      private static String token;

      public static String get() {
            return token;
      }

      public static void set(String tkn) throws EmptyTokenException {
            token = tkn;
      }

}
