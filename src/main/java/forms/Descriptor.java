package forms;

public interface Descriptor<E extends FormData> {

      /**
       * Compares the DB version with local version using
       * date. Sets the data to the most recent version.
       */
      void setMostRecent();

      /**
       * Helper method to setMostRecent
       *
       * @return the timeStamp in millis of a local meta file
       * * if exists. If not returns 0.
       */
      long getLocalDataDate();

      void setToLocalData();

      boolean setToRemoteData();

      //public String[] parseResponse(String[] response);

      void setProperties(final E fm);

      /**
       * Sets the defaultProperties
       */
      void setProperitesDefault();


      /**
       * Clears the form and sets
       * to default values.
       */
      void clear();

}
