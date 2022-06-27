package fmtree;

/**
 * This class modifies the original and extends the BinarySearchTree by adding the rotate
 * operations and tracking the tree points in BinarySearchTree. Rotation will change the balance of a search
 * tree while preserving the search tree property and tracking the far left, tree root, and
 * far right points. Tracking the points enables O(1) access to these points without needing
 * to recursively move from the top of the tree or from any point in the tree to the tip.
 * Used as a common base class for self-balancing trees.
 *
 * @author Lowell Stadelman, modified from Koffman and Wolfgang
 */
public class BinarySearchTreeWithRotate<E extends Comparable<E>> extends BinarySearchTree<E> {

      /**
       * Method to perform a right rotation.
       * pre-condition  root is the root of a binary search tree.
       * post-condition root.right is the root of a binary search tree,
       * root.right.right is raised one level,
       * root.right.left does not change levels,
       * root.left is lowered one level,
       * the new root is returned.
       * root.parent is changed to right.parent
       *
       * @param node The node to be rotated
       * @return The new root of the rotated sub-tree
       */
      protected Node<E> rotateRight(Node<E> node) {
            Node<E> temp = node.left;
            node.left = temp.right;
            try {
                  node.left.parent = node;
            } catch (NullPointerException e) {
                  //
            }
            temp.parent = node.parent;
            node.parent = temp;
            //temp.parent.left = temp;
            temp.right = node;
            return temp;
      }

      /**
       * Method to perform a left rotation.
       * pre-condition  root is the root of a binary search tree.
       * post-condition root.left is the root of a binary search tree,
       * root.left.left is raised one level,
       * root.left.right does not change levels,
       * root.rigth is lowered one level,
       * root.parent is changed to left.parent
       * the new root is returned.
       *
       * @return The new root of the rotated tree
       */
      protected Node<E> rotateLeft(Node<E> node) {

            Node<E> temp = node.right;
            //temp.parent =
            node.right = temp.left;

            try {
                  node.right.parent = node;
            } catch (NullPointerException e) {
                  //System.out.println("Null pointer exception in FMTree rotateLeft()");
                  //System.out.println("node.right.parent = null");
            }
            //}
            temp.parent = node.parent;
            node.parent = temp;

            temp.left = node;

            return temp;
      }
}
