package fmtree;

import flashmonkey.CompareFCard;
import flashmonkey.FlashCardMM;
import type.testtypes.GenericTestType;
import type.testtypes.TestList;

/**
 * FMTWalker adds a variable linking to the parent node and navigation methods.
 * <p>
 * FMTWalker uses a balanced binary tree to contain the structure. This stucture is used
 * for the convenience of adding and deleting cards from the center of the data-
 * structure. This structure allows for O(log(n)) adds when a flashcard needs to be
 * added to the structure or deleted. The getMethods in this class allow for the
 * button operations making the overall UI convenient to users.
 * The balanced binary search tree is based on the Adelson-Velskii and Landis AVLtree.
 *
 * @author Lowell Stadelman. Class modified from Koffman and Wolfgang AVLTree
 */
public final class FMTWalker<T extends Comparable<T>> extends BinarySearchTreeWithRotate<T> {

      private static FMTWalker CLASS_INSTANCE;

      // The current node being used.
      private static Node currentNode;
      // The extreme right and left nodes.
      // In the current configuration left is lowest.
      private static Node lowestNode;
      private static Node highestNode;
      private boolean heightChanged;
      private static int nodeCount = 0;

      /**
       * no arg constructor
       */
      private FMTWalker() { /* do nothing */ }

      /**
       * Returns thread safe Singleton instance of FMTWalker
       *
       * @return If no other instance of FMTWalker is found,
       * returns a new FMTWalker.
       */
      public synchronized static FMTWalker getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new FMTWalker();
            }
            return CLASS_INSTANCE;
      }

      /**
       * The current node to be used outside of the class
       *
       * @return
       */
      public static Node getCurrentNode() {
            return currentNode;
      }

      /**
       * Returns the number of nodes in this tree.
       *
       * @return
       */
      public static int getCount() {
            return nodeCount;
      }

      /**
       * Returns the lowestChild in the tree.
       * NOTE: Caution, may be stale data
       * setHighLow() should be called, if needed, to get current
       * loweest data.
       * Caution is advised if performance is an issue.
       */
      public static Node getLowestNode() {
            return lowestNode;
      }

      /**
       * Returns the highestChild in the tree.
       * NOTE: Caution, may be stale data
       * setHighLow() should be called, if needed, to get current
       * highest data.
       * Caution is advised if performance is an issue.
       */
      public static Node getHighestNode() {
            return highestNode;
      }

      // *** SETTERS ***

      /**
       * Setter for the current node to be used
       * outside of the class.
       *
       * @param n
       */
      public static void setCurrentNode(Node n) {
            currentNode = n;
      }

      public void setCurrentNode(FlashCardMM currentCard) {
            T fc = (T) currentCard;
            Node n = findNode(fc);
            setCurrentNode(n);
      }

      /**
       * Sets the lowestNode and highestNode to the appropriate/respective
       * locations in the tree.
       */
      public void setHighLow() {
            highestNode = getHighestChild(root);
            lowestNode = getLowestChild(root);
      }

      public boolean isEmpty() {

            return root == null;
      }

      /**
       * add starter method. Swaps the last node with
       * parent node making the parent node the last
       * node for the next iteration.
       *
       * @param item
       * @return
       */
      @Override
      public boolean add(T item) {

            this.heightChanged = false;
            this.root = add(root, item, null);
            //tempParent = root;
            return this.addReturn;
      }

      /**
       * Recursive add method. Inserts the given object into the tree.
       * post-conditions addReturn is set true if the item is inserted,
       * false if the item is already in the tree.
       *
       * @param localRoot The local root of the subtree
       * @param fc        this flashCard
       * @param parent,   this nodes parent node
       * @return The new local root of the subtree with the item
       * inserted
       */
      private Node<T> add(Node<T> localRoot, T fc, Node<T> parent) {
            if (localRoot == null) {
                  addReturn = true;
                  heightChanged = true;
                  nodeCount++;

                  try {
                        return new Node(fc, parent);
                  } catch (NullPointerException e) {
                        return new Node(fc);
                  }
            }

            int compareInt = fc.compareTo(localRoot.data);
            if (compareInt == 0) {
                  // Item is already in the tree.
                  heightChanged = false;
                  addReturn = false;

                  return localRoot;
            } else if (compareInt < 0) {
                  // item is less than data
                  localRoot.left = add(localRoot.left, fc, localRoot);
                  if (heightChanged) {
                        decrementBalance(localRoot);
                        if (localRoot.balance < -1) {
                              heightChanged = false;
                              return rebalanceLeft(localRoot);
                        }
                  }
                  return localRoot; // Rebalance not needed.
            } else {  // if greater than 0
                  localRoot.right = add(localRoot.right, fc, localRoot);
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
       * pre-conditions localRoot is the root of an ThreeWayTree subtree that is
       * critically left-heavy.
       * post-conditions Balance is restored.
       *
       * @param localRoot Root of the FMTWalker subtree
       *                  that needs rebalancing
       * @return a new localRoot
       */
      private Node<T> rebalanceLeft(Node<T> localRoot) {
            // Obtain reference to left child.
            Node<T> leftChild = localRoot.left;
            // If left heavy?
            if (leftChild.balance > 0) {
                  Node<T> leftRightChild = leftChild.right;

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

            return rotateRight(localRoot);
      }

      /**
       * Method to rebalance right.
       * pre-conditions localRoot is the root of an ThreeWayTree subtree that is
       * critically right-heavy.
       * post-conditions Balance is restored.
       *
       * @param localRoot Root of the FMTWalker subtree
       *                  that needs rebalancing
       * @return a new localRoot
       */
      private Node<T> rebalanceRight(Node<T> localRoot) {
            // Obtain referance to right child
            Node<T> rightChild = localRoot.right;

            // see if right-left heavy
            if (rightChild.balance < 0) {
                  // obtian referance to right-left child
                  Node<T> rightLeftChild = rightChild.left;

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
                  localRoot.right = rotateRight(rightChild);

            } else { // Right-right child case
                  rightChild.balance = 0;
                  localRoot.balance = 0;
            }
            // Now rotate the local root left.
            return rotateLeft(localRoot);
      }

      /**
       * Method to decrement the balance field and to reset the value of
       * changed.
       * pre-conditions The balance field was correct prior to an insertion [or
       * removal,] and an item is either been added to the left[
       * or removed from the right].
       * post-conditions The balance is decremented and the increase flags is set
       * to false if the overall height of this subtree has not
       * changed.
       *
       * @param node The TW node whose balance is to be incremented
       */
      private void decrementBalance(Node<T> node) {
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
       * pre-conditions The balance field was correct prior to an insertion [or
       * removal,] and an item has either been added to the right[
       * or removed from the left].
       * post-conditions The balance is incremented and the increase flags is set
       * to false if the overall height of this subtree has not
       * changed.
       *
       * @param node The TW node whose balance is to be incremented
       */
      private void incrementBalance(Node<T> node) {
            // Increment the balance
            node.balance++;
            if (node.balance == 0) {
                  // if now balanced, overall height has not increased.
                  heightChanged = false;
            }
      }

      /**
       * Clear the tree
       * pre-conditions Assumes that either tree.root is null or that
       * .parent, .right, .left, and .data have been initilized
       * to some value.
       */
      public void clear() {

            if (this.root != null) {
                  this.root.parent = null;
                  this.root.right = null;
                  this.root.left = null;
                  this.root.data = null;
                  this.root = null;  // Trying to keep the reference
                  nodeCount = 0;
            }
      }

      /**
       * Sets currentNode to the lowest child in the tree
       */
      public void setToFirst() {
            currentNode = getLowestChild(this.root);
      }

      /**
       * Sets currentNode to the last child in the tree
       */
      public void setToLast() {
            currentNode = getHighestChild(this.root);
      }

      /**
       * This method has been tuned specifically for T.
       * Returns the next highest node(by its flash card QNumber) in the tree.
       * -- !!! This is a complex operation !!! ---
       * since recursive trees do not offer a great way to return the next
       * higher node, and retain an easy way to get back to this node. We use
       * a three way tree.
       * pre-conditions Expects the EncryptedUser.EncryptedUser of this method to handle the NullPointerException.
       *
       * @throws NullPointerException
       */
      //public Node<T> getNext(Node<T> n) {
      public void getNext() {

            CompareFCard compare = new CompareFCard();
            Node local = currentNode;

            try {
                  if (local.right == null) {
                        //subtract the parents QNumber from this cards QNumber
                        if (compare.compare(local, local.parent) < 0) {
                              //return (Node) getNext(n.parent);
                              local = local.parent;
                        }
                        // Is this needed?
                        // Changed 10 Nov 2017
                        // else if(n.left == null && n.right == null) {
                        else if (local.right == null) {
                              local = getLgParent(local, (T) local.data);
                        }
                  } else { // there is a right node
                        // is there a right child?
                        if (local.right != null && local.right.left == null) {
                              local = local.right;
                        } else if (local.right.left != null) {
                              local = getLowestChild(local.right.left);
                        }
                        // Is this ever used? Yes
                        else {
                              local = getLgParent(local, (T) local.data);
                        }
                  }
            } catch (NullPointerException e) {

            } finally {
                  currentNode = local;
            }
      }

      /**
       * Returns the next lowest node in the tree. This is a complex operation
       * since recursive trees do not offer a great way to return the next
       * lowest node, and retain an easy way to get back to this node.
       */
      public void getPrevious() {

            CompareFCard compare = new CompareFCard();
            Node local = currentNode;

            try {
                  if (currentNode.left == null) {
                        //subtract the parents QNumber from this cards QNumber
                        if (compare.compare(currentNode, currentNode.parent) > 0) {
                              //return (Node) getNext(n.parent);
                              local = currentNode.parent;
                        }
                        // Changed 10 nov 2017
                        //else if(n.right == null && n.left == null) {
                        else if (currentNode.left == null) {
                              local = getSmParent(currentNode, (T) currentNode.data);
                        }
                  }
                  // there is no left node
                  else {
                        // is there a right child?
                        if (currentNode.left != null && currentNode.left.right == null) {
                              local = currentNode.left;
                        } else if (currentNode.left.right != null) {
                              local = getHighestChild(currentNode.left.right);
                        } else {
                              local = getSmParent(currentNode, (T) currentNode.data);
                        }
                  }
            } catch (NullPointerException e) {
                  //return getLowestChild(n.right);
            } finally {
                  currentNode = local;
                  //return local;
            }
      }


      //Methods

      /**
       * Starter method find.
       *
       * @param fc The flashCard being sought
       * @return The object, if found, otherwise null
       */
      public Node findNode(T fc) {
            return findNode(root, fc);
      }

      /**
       * findNode finds the node containing the target integer.
       *
       * @return Returns an FMTree node
       */
      public Node findNode(FMTree.Node localRoot, T target) {
            if (localRoot == null) {
                  return null;
            }
            // Compare the target with the data field at the root.
            int compResult = target.compareTo((T) localRoot.data);
            if (compResult == 0) {
                  return localRoot;
            } else if (compResult < 0) {
                  return findNode(localRoot.left, target);
            } else {
                  return findNode(localRoot.right, target);
            }
      }

      // *** HELPER METHODS ***

      /**
       * getLowestChild recursively gets the lowest card from the left branch
       *
       * @return Returns the lowest card from the left branch beneath the
       * parent node in the parameter.
       */
      private Node getLowestChild(Node node) {
            try {
                  if (node.left == null) {
                        return node;
                  }
                  return getLowestChild(node.left);
            } catch (NullPointerException e) {
                  // do nothing
            }
            return node; // Hmmmmm was node.parent ??? Whaaaat????
      }

      /**
       * recursively gets the highest card from the right branch
       *
       * @param node Tree root
       * @return Returns the highest card from the right branch beneath the
       * parent node in the parameter.
       */
      private Node getHighestChild(Node node) {
            try {
                  if (node.right == null) {
                        return node;
                  }
                  return getHighestChild(node.right);
            } catch (NullPointerException e) {
                  // do nothing
            }

            return node;
      }

      /**
       * Gets the next larger parent in the tree above
       *
       * @param node
       * @param value, The value that the comparitor uses. IE if an int
       * @return the next larger parent node
       */
      private Node getLgParent(Node<T> node, T value) {
            if (value.compareTo(node.data) < 0) {
                  return node;
            }
            return getLgParent(node.parent, value);
      }


      private Node getSmParent(Node<T> node, T value) {
            // base case
            if (value.compareTo(node.data) > 0) {
                  return node;
            }
            return getSmParent(node.parent, value);
      }

      /**
       * Calculates the highest possible points
       * for the number of cards and TestTypes in
       * the tree. Not all TestTypes are scoreable
       * thus we check if the TestType is a scoreable
       * Type as well as if the FlashCardMM was
       * re-inserted into the deck.
       *
       * @return
       */
      public double highestPossibleScore() {
            Score score = new Score();
            return score.highestPossible();
      }

      private class Score extends FMTree<FlashCardMM> {

            public Score() {
                  /* no args */
            }

            public double highestPossible() {
                  // traverse through the tree
                  // and get the score from the data
                  // in the node.
                  double score = 0;
                  return inOrderTraverseForScore(root, score);
            }

            private double inOrderTraverseForScore(Node<FlashCardMM> node, double num) {
                  if (node == null) {
                  } else {
                        inOrderTraverseForScore(node.left, num);
                        num += getValue(node.data);
                        inOrderTraverseForScore(node.right, num);
                        num += getValue(node.data);
                  }
                  return num;
            }

            /**
             * if a card can be scored then return a score.
             * If the cardNumber is divisible by 10, score is
             * 2 points, else it is .5 points.
             *
             * @param fc
             * @return 0, .5, or 2
             */
            private double getValue(FlashCardMM fc) {
                  GenericTestType t = TestList.selectTest(fc.getTestType());
                  return t.score();
            }
      }
}
