package type;

public interface DnDInterface {

   /**
    * This method calls all the other methods, so that it can be initialized
    * easier.
    */

   default void init() {
      registerDndOperations();
   }

   void registerDndOperations();
}
