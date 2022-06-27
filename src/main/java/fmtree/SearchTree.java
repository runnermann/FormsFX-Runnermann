/*
 * AUTHOR: Lowell Stadelman
 * COURSE: CS-113 Data Structures, Abstraction and Design / Java
 */

package fmtree;

/*******************************************************************************
 * CLASS DESCRIPTION: This is the SearchTree interface ....
 ******************************************************************************/


public interface SearchTree<E> {
      /**
       * Inserts item where it belongs in the tree.
       *
       * @param item
       * @return Returns true if the item is
       * inserted; False if it isn't in the tree
       */
      boolean add(E item);

      /**
       * Returns true if the target is found in the tree
       *
       * @param target
       * @return Returns true if the target is found in the tree
       */
      boolean contains(E target);

      /**
       * Returns a reference to the data in the node that is equal to the target.
       *
       * @param target
       * @return Returns a referance or null if the target is not found
       */
      E find(E target);

      /**
       * Removes target(if found) from thtree and returns it;
       *
       * @param target
       * @return If the target is found it is removed and it is returned if it
       * is found
       */
      E delete(E target);

      /**
       * REmoves target (if found) from tree and returns true;
       *
       * @param target
       * @return if found in the tree it returns true; otherwise it returns false.
       */
      boolean remove(E target);


}
