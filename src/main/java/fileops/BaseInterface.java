package fileops;

public interface BaseInterface<T extends BaseInterface> {


      /**
       * <p>IMPORTANT!!! Implement this method</p>
       * <p>Check if This singleton class has been instantiated before calling
       *        a method for verifying state. No need to instantiate
       *        the implemented Class to verify if it exists.</p>
       * <pre>
       *     Should look similar to
       *     public static boolean instanceExists() {
       *             return CLASS_INSTANCE != null;
       *       }
       * </pre>
       *
       * @return true if the CLASS_INSTANCE exists.
       */


      /**
       * Deck save action for this class that is called when FlashMonkey main stage
       * is closed. May be used outside of the class. Single point call
       * that is always called when class stage is closed. Should be implemented
       * by any class that contains files to be saved to file or the cloud.
       * E.g. ReadFlash and CreateFlash.
       * @return If there is a scene change depending on an answer from the user,
       * true to continue, false to stop.
       */
      boolean saveOnExit();

      /**
       * To be called by the class on stage.onHidden()
       * Closes this class. Clears the tree. Closes the treeWindow. Does not save
       * the current work.
       */
      void onClose();
}
