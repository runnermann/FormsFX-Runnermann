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
            if(tkn.length() < 64) throw new EmptyTokenException("WARNING: Token is less than 64 chars in length." +
                " Token length: " + tkn.length());
            token = tkn;
      }

      public static void clearToken() {
            token = "123456789123456789123456789";
            token = null;
      }
}
