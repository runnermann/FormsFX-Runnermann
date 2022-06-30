package fileops;

public interface BaseInterface {

      /**
       * Deck save action for this class that is called when FlashMonkey main stage
       * is closed. May be used outside of the class. Single point call
       * that is always called when class stage is closed. Should be implemented
       * by any class that contains files to be saved to file or the cloud.
       * E.g. ReadFlash and CreateFlash.
       */
      void saveOnExit();

      /**
       * To be called by the class on stage.onHidden()
       * Closes this class. Clears the tree. Closes the treeWindow. Does not save
       * the current work.
       */
      void onClose();
}
