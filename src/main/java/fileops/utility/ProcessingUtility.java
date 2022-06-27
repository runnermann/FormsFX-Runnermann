package fileops.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProcessingUtility {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ProcessingUtility.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingUtility.class);

      private ProcessingUtility() { /* no args */}

      /**
       * Parses a response from HTTP and returns it
       * as a set. Each item is unique/will only exist once.
       *
       * @param response
       * @return
       */
      public static Set<String> httpParse(String response) {
            LOGGER.debug("response: {}", response);
            String res = response.substring(1, response.length() - 1);
            LOGGER.debug("RESPONSE: " + res);
            String[] rAry = res.split(",");
            Set<String> e = new HashSet<>(rAry.length);
            for (int i = 0; i < rAry.length; i++) {
                  e.add(rAry[i].substring(1, rAry[i].length() - 1));
            }
            return e;
      }

      /**
       * Converts from an array to an arrayList
       *
       * @param strAry
       * @return Converts all items including null; Elements
       * can contain null entities in return.
       */
      public static ArrayList<String> convert(String[] strAry) {
            ArrayList<String> ret = new ArrayList<>(strAry.length);
            Arrays.stream(strAry)
                .forEach(ret::add);
            return ret;
      }

      /**
       * Converts from arraylist to array
       *
       * @param str
       * @return Converts all items including null; Elements
       * can contain null entities in return.
       */
      public static String[] convert(ArrayList<String> str) {
            String[] ret = new String[str.size()];
            for (int i = 0; i < str.size(); i++) {
                  ret[i] = str.get(i);
            }
            return ret;
      }
}
