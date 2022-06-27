/*
 * AUTHOR: Lowell Stadelman
 * Based on BinaryTree from Koffman and Wolfgang
 */

package fmtree;

/*******************************************************************************
 * CLASS DESCRIPTION: This is the BinaryTree with a parent. It contains the methods needed
 * to create a binary tree.
 *
 * @author Lowell Stadelman
 ******************************************************************************/

public class FMTree<E> {
      /*** NODE OBJECT ***/
      protected Node<E> root;

      protected int balance;


      /**
       * Default Constructor: creates an empty node.
       */
      public FMTree() {
            root = null;
            //parent = null;
            balance = 0;
      }


      /**
       * Constructor: Creates the currentNode node with only
       * the node root. parent remain null.
       *
       * @param root
       */
      public FMTree(Node<E> root) {
            this.root = root;
            //this.parent = null;
            this.balance = 0;
      }

      /**
       * Constructor: Creates the currentNode node with
       * the parent root.
       *
       * @param root
       * @param parent
       */
      public FMTree(Node<E> root, Node<E> parent) {
            this.root = root;
            this.balance = 0;
      }

      /**
       * Constructor: Creates a full node with left and right leafs
       *
       * @param data
       * @param leftTree
       * @param rightTree
       */
      public FMTree(E data, FMTree<E> leftTree, FMTree<E> rightTree) {
            root = new Node<E>(data);
            if (leftTree != null) {
                  root.left = leftTree.root;
            } else {
                  root.left = null;
            }

            if (rightTree != null) {
                  root.right = rightTree.root;
            } else {
                  root.right = null;
            }
      }

      /**
       * setRoot sets the tree root to the parameter
       *
       * @param root
       */
      public void setRoot(Node<E> root) {
            this.root = root;
      }

      /**
       * Getter for left sub tree
       *
       * @return BinaryTree left currentNode or null if empty
       */
      public FMTree<E> getLeftSubtree() {
            if (root != null && root.left != null) {
                  return new FMTree<E>(root.left);
            } else {
                  return null;
            }
      }

      /**
       * Getter for right sub tree
       *
       * @return BinaryTree right currentNode or null if empty
       */
      public FMTree<E> getRightSubtree() {
            if (root != null && root.right != null) {
                  return new FMTree<E>(root.right);
            } else {
                  return null;
            }
      }

      /**
       * Return the data field of the root
       *
       * @return the data field of the root
       * or null if the root is null
       */
      public E getData() {
            if (root != null) {
                  return root.data;
            } else {
                  return null;
            }
      }

      /**
       * Use with caution
       *
       * @return
       */
      public Node<E> getRoot() throws IllegalStateException {
            if (root != null) {
                  return root;
            }
            throw new IllegalStateException("The tree root is null.");
      }

      /**
       * Checks to see if node is a leaf
       *
       * @return boolean true if node subtrees are null
       */
      public boolean isLeaf() {
            return (root.left == null && root.right == null);
      }


      /**
       * toString method. Uses pre order traversal method
       *
       * @return Returns a string
       */
      @Override
      public String toString() {
            StringBuilder sb = new StringBuilder();
            preOrderTraverse(root, 1, sb);
            return sb.toString();
      }


      /**
       * Perform a preorder traversal.
       *
       * @param node  The local root
       * @param depth The depth
       * @param sb    The string buffer to save the output
       */
      public void preOrderTraverse(Node<E> node, int depth,
                                   StringBuilder sb) {
            for (int i = 1; i < depth; i++) {
                  sb.append("  ");
            }
            if (node == null) {
                  sb.append("null\n");
            } else {
                  sb.append(node);
                  sb.append("\n");
                  preOrderTraverse(node.left, depth + 1, sb);
                  preOrderTraverse(node.right, depth + 1, sb);
            }
      }

      public String inOrderTraverse() {
            StringBuilder sb = new StringBuilder();
            return "In order traverse: " + inOrderTraverse(this.root, sb);
      }

      /**
       * In order traversal. Prints the tree in order.
       *
       * @param node:
       */
      protected String inOrderTraverse(Node<E> node, StringBuilder sb) {

            if (node == null) {
            } else {
                  inOrderTraverse(node.left, sb);
                  sb.append(node.data + ", ");
                  inOrderTraverse(node.right, sb);
            }
            return sb.toString();
      }

      /**
       * ***** INNER CLASS ******
       * Inner class used to create a binary tree node
       * Contains Node left and right. Contains full
       * constructor and toString method.
       */
      public static class Node<E> {
            protected E data;
            protected Node<E> parent;
            public Node<E> left;
            public Node<E> right;
            protected int balance;

            /**
             * Constructor. Sets data to data and left and
             * right references.
             *
             * @param data Type
             */
            public Node(E data) {
                  this.data = data;
                  parent = null;
                  left = null;
                  right = null;
                  balance = 0;
            }

            /**
             * Constructor. Sets data to data and parent referances
             *
             * @param data
             * @param parent
             */
            public Node(E data, Node parent) {
                  this.data = data;
                  this.parent = parent;
                  left = null;
                  right = null;
                  balance = 0;
            }

            public E getData() {
                  return this.data;
            }

            /**
             * toString method
             *
             * @return String
             */
            public String toString() {
                  return this.data.toString();
            }

            /**
             * getParent gets the next greater parent
             *
             * @return the TWnode from the next greater parent
             */
            public Node<E> getParent() {
                  return this.parent;
            }

      }
      // ***** END INNER CLASS *****
}
