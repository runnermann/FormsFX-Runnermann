package fmtree;

/**
 * Self-balancing binary search tree using the algorithm defined
 * by Adelson-Velskii and Landis.
 *
 * @author Lowell Stadelman adopted from Koffman and Wolfgang
 */
public class AVLTree<E extends Comparable<E>> extends BinarySearchTreeWithRotate<E> {

      // Data Fields
    /** Indicates that height of tree has increased. */
      private boolean heightChanged;
      int size = 0;

      /**
       * add starter method.
       * pre-condition the item to insert implements the Comparable interface.
       *
       * @param item The item being inserted.
       * @return true if the object is inserted; false
       * if the object already exists in the tree
       * @throws ClassCastException if item is not Comparable
       */
      @Override
      public boolean add(E item) {
            heightChanged = false;
            root = add((AVLNode<E>) root, item);
            return addReturn;
      }

      /**
       * Recursive add method. Inserts the given object into the tree.
       * post-condition addReturn is set true if the item is inserted,
       * false if the item is already in the tree.
       *
       * @param localRoot The local root of the subtree
       * @param item      The object to be inserted
       * @return The new local root of the subtree with the item
       * inserted
       */
      public AVLNode<E> add(AVLNode<E> localRoot, E item) {
            size++;

            if (localRoot == null) {
                  addReturn = true; // formerly true
                  heightChanged = true; // formerly true
                  return new AVLNode<E>(item);
            }

            if (item.compareTo(localRoot.data) == 0) {
                  // Item is already in the tree.
                  heightChanged = false;
                  addReturn = false;
                  return localRoot;
            } else if (item.compareTo(localRoot.data) < 0) {
                  // item < data
                  localRoot.left = add((AVLNode<E>) localRoot.left, item);

                  if (heightChanged) {
                        decrementBalance(localRoot);
                        if (localRoot.balance < -1) {
                              heightChanged = false;
                              return rebalanceLeft(localRoot);
                        }
                  }
                  return localRoot; // Rebalance not needed.
            } else {  // > 0

                  localRoot.right = add((AVLNode<E>) localRoot.right, item);

                  if (heightChanged) {
                        incrementBalance(localRoot);
                        if (localRoot.balance > 1) {
                              heightChanged = false;
                              return rebalanceRight(localRoot);
                        }
                  }
                  return localRoot;
            }
      }


      /**
       * Method to rebalance left.
       * pre-condition localRoot is the root of an AVL subtree that is
       * critically left-heavy.
       * post-condition Balance is restored.
       *
       * @param localRoot Root of the AVL subtree
       *                  that needs rebalancing
       * @return a new localRoot
       */
      public AVLNode<E> rebalanceLeft(AVLNode<E> localRoot) {
            // Obtain reference to left child.
            AVLNode<E> leftChild = (AVLNode<E>) localRoot.left;
            // If left heavy?
            if (leftChild.balance > 0) {
                  AVLNode<E> leftRightChild = (AVLNode<E>) leftChild.right;

                  // If Left-Rigth-Left
                  if (leftRightChild.balance < 0) {

                        leftChild.balance = 0;

                        leftRightChild.balance = 0;
                        localRoot.balance = 1;
                        // If Left-right-right
                  } else if (leftRightChild.balance > 0) {

                        leftChild.balance = -1;

                        leftRightChild.balance = 0;
                        localRoot.balance = 0;
                  } else {

                        leftChild.balance = 0;
                        localRoot.balance = 0;
                  }
                  // Perform left rotation.

                  localRoot.left = rotateLeft(leftChild);
            } else {
                  //Left-Left case
                  leftChild.balance = 0;
                  localRoot.balance = 0;
            }

            return (AVLNode<E>) rotateRight(localRoot);
      }

      /**
       * Method to rebalance right.
       * pre-condition localRoot is the root of an AVL subtree that is
       * critically right-heavy.
       * post-condition Balance is restored.
       *
       * @param localRoot Root of the AVL subtree
       *                  that needs rebalancing
       * @return a new localRoot
       */
      protected AVLNode<E> rebalanceRight(AVLNode<E> localRoot) {
            // Obtain referance to right child
            AVLNode<E> rightChild = (AVLNode<E>) localRoot.right;

            // see if right-left heavy
            if (rightChild.balance < 0) {
                  // obtian referance to right-left child
                  AVLNode<E> rightLeftChild = (AVLNode<E>) rightChild.left;

                  // Adjust the balances to be their new values after
                  // the rotations are performed.
                  if (rightLeftChild.balance > 0) {
                        rightChild.balance = 0;
                        rightLeftChild.balance = 0;
                        localRoot.balance = -1;

                  } else if (rightLeftChild.balance < 0) {
                        rightChild.balance = 1; // changed from RIGHT_HEAVY
                        rightLeftChild.balance = 0;
                        localRoot.balance = 0;

                  } else {
                        rightChild.balance = 0;
                        localRoot.balance = 0;

                  }

                  // Perform right rotation
                  ///System.out.println("right rotation");
                  localRoot.right = rotateRight(rightChild);

            } else { // Right-right child case

                  rightChild.balance = 0;
                  localRoot.balance = 0;
            }
            // Now rotate the local root left.
            ///System.out.println("rotate left " + localRoot.toString());
            return (AVLNode<E>) rotateLeft(localRoot);
      }

      /**
       * Method to decrement the balance field and to reset the value of
       * changed.
       * pre-condition The balance field was correct prior to an insertion [or
       * removal,] and an item is either been added to the left[
       * or removed from the right].
       * post-condition The balance is decremented and the increase flags is set
       * to false if the overall height of this subtree has not
       * changed.
       *
       * @param node The AVL node whose balance is to be incremented
       */
      public void decrementBalance(AVLNode<E> node) {
            // Decrement the balance.
            node.balance--;
            if (node.balance == 0) {
                  // If now balanced, overall height has not increased.
                  heightChanged = false;
            }
      }

      /**
       * Method to increment the balance field and to reset the value of
       * changed.
       * pre-condition The balance field was correct prior to an insertion [or
       * removal,] and an item has either been added to the right[
       * or removed from the left].
       * post-condition The balance is incremented and the increase flags is set
       * to false if the overall height of this subtree has not
       * changed.
       *
       * @param node The AVL node whose balance is to be incremented
       */
      public void incrementBalance(AVLNode<E> node) {
            // Increment the balance
            node.balance++;
            if (node.balance == 0) {
                  // if now balanced, overall heigght has not increased.
                  heightChanged = false;
            }
      }

      /**
       * CLear the tree
       *
       * @param tree : The tree to be cleared
       */
      public void clear(AVLTree<E> tree) {
            tree.root.right = null;
            tree.root.left = null;
            tree.root.data = null;
      }

      /**
       * INNER CLASS AVL Node. Extends the
       * BinaryTree.Node by adding the balance field.
       */
      public static class AVLNode<E extends Comparable<E>> extends Node<E> {

            public int balance;

            // Methods

            /**
             * Construct a node with the given item as the data field.
             *
             * @param item The data field
             */
            public AVLNode(E item) {
                  super(item);
                  balance = 0;
            }

            /**
             * Return a string representation of this object.
             * The balance value is appended to the contents.
             *
             * @return String representation of this object
             */
            @Override
            public String toString() {
                  return super.toString() + ":    " + balance;
            }
      } //  *** END INNER CLASS ***
}
