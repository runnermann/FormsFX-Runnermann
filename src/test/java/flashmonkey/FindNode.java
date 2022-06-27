package flashmonkey;

import ch.qos.logback.classic.Level;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

//public class FindNode extends RecursiveAction {
public class FindNode {
    
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FindNode.class);
    
    
    private int limit;
    private Node fxNode;
    private Scene scene;
    //private Pane paneForNode;
    //Node node;
    Pane root;
    Map<Node, Integer> map;

    public FindNode() {
        LOGGER.setLevel(Level.DEBUG);

        limit = 0;
        HBox testHBox = new HBox();
        scene = new Scene(testHBox);
        //node = (Node) paneForNode;
    }



    public Node findNodeInPaneGraphStarter(Node findNode, Stage window) {
    
        LOGGER.debug("findNodeInPaneGraphStarter(node, window) called\n");
        Scene scene = window.getScene();
        Parent parent = scene.getRoot();
        map = new HashMap<>();
        GridPane gp = (GridPane) parent.getChildrenUnmodifiable().get(0);

        if(gp.getClass().isInstance(new GridPane())) {
            LOGGER.debug("In findNode and Pane is a gridpane");

            // BorderPane bp = (BorderPane) root;
            Node newNode = (Pane) gp.getChildren().get(1);
            map.put(newNode, 0);
            return findNodeInPaneGraph(findNode, newNode, gp, 0);
        }
        // find this node, the first child node, the root node
        //map.put(root, 0);
        return findNodeInPaneGraph(findNode, root.getChildren().get(0), root, 0);
    }


    /**
     * Finds a node in the Pane graph. Not an efficient
     * way to search a list. Use if the node being searched
     * for is in one of the branches.
     *
     * @param findNode A child type of Node. i.e. "new ImageView()"
     *                 if searching for an ImageView
     * @param pane The Pane that the node is contained in.
     * @return Returns node if found, otherwise returns null.
     */

    public Node findNodeInPaneGraphStarter(Node findNode, Pane pane) {
    
        // System.out.println("\nfindNodeInPaneGraphStarter(node, pane) called\n");
        root = pane;
        map = new HashMap<>();
        map.put(pane, 0);

        return findNodeInPaneGraph(findNode, root.getChildren().get(0), root, 0);
    }



    int threadNum = 0;
    //Thread thread = new Thread();
    Node returnNode = null;

    private Node findNodeInPaneGraph(Node findMe, Node childNode, Pane parentPane, int idx ) {
        System.out.println("\nfindNodeInPaneGraph(findMe, node, pane) called\n");
    
         System.out.println(" Searching for: " + findMe.getClass().getName());
         System.out.println(" This Pane is class: " + parentPane.getClass().getName() +
              "\n Num children in this pane: " + parentPane.getChildren().size() + " idx: " + idx +
              "\n Start child node is: " + childNode.getClass().getName() +
         "\n childNode has num children: " + ((Pane) childNode).getChildren().size());

        printChildren(parentPane.getChildren().size() - 1, parentPane);
        if(parentPane.getParent() != null) {
             System.out.println("FYI, Parent is not null: pane.getParent(): " + parentPane.getParent().getClass().getName());
        }
        System.out.println("line 96");

        // If map returns idx > size
        if (childNode == root && map.get(childNode) >= root.getChildren().size()) {
            System.out.println("returning null");
            returnNode = null;
        }
        // if thisNode is the node we are looking for
        else if (childNode.getClass().getName().equals(findMe.getClass().getName())) {
      //  else if (true) {
            System.out.println("Found it, returning: " + childNode.getClass().getName());
            returnNode = childNode;
        }
        // else if thisNode is a Pane
        else if (childNode.getClass().isInstance(new HBox())
                || childNode.getClass().isInstance(new VBox())
                || childNode.getClass().isInstance(new Pane())
                || childNode.getClass().isInstance(new BorderPane())
                || childNode.getClass().isInstance(new GridPane())) {

            Pane pane;

            System.out.println("node is: " + childNode.getClass().getName());

            if (childNode.getClass().getName().equals("BorderPane")) {
                System.out.println("BorderPane selected");

                BorderPane bp = (BorderPane) childNode;
                pane = bp;
                Node newNode = (Node) bp.getCenter();
                findNodeInPaneGraph(findMe, newNode, bp, 0);
            } else if (childNode.getClass().getName().equals("GridPane")) {
                System.out.println("GridPane selected");

                GridPane gp = (GridPane) childNode;
                pane = gp;

                Bounds bounds = gp.getBoundsInLocal();
                Bounds screenBounds = gp.localToScreen(bounds);

                System.out.println("gp data " + screenBounds.toString());

            } else if(childNode.getClass().isInstance(new VBox())) {
                VBox vBox = (VBox) childNode;
                printChildren(vBox.getChildren().size(), vBox);
                return vBox.getChildren().get(0);

            }
            else {
                System.out.println("pane is now childNode");
                pane = (Pane) childNode;
                System.out.println("childNode.getName: " + childNode.getClass().getName());
            }
    
             System.out.println("line 144");
            
            map.put(pane, 0);
            // get the first child in the Pane.
            Node newNode = pane.getChildren().get(0);

             System.out.println(childNode.getClass().getName() + " at idx " + idx +
                    " spawned, new recursive loop for: " + newNode.getClass().getName() +
                    " \n numChildren: " + pane.getChildren().size());
            findNodeInPaneGraph(findMe, newNode, pane, 0);
    
            System.out.println("line 155");
        } else {
            
            try {
                // continue
                // System.out.println("Going back up to parent, ParentPane child size is: " + parentPane.getChildren().size() + " idx: " + idx);

                // If idx < pane, increment to the next child in the pane.
                if ((idx + 1) < parentPane.getChildren().size()) {

                    // System.out.println("idx is < size()");
                    ++idx;
                    findNodeInPaneGraph(findMe, parentPane.getChildren().get(idx), parentPane, idx);
                // else get the next parent with available children, increment its index, and get its
                // next child.
                } else {
                    // System.out.println("idx is >= size()");

                    // get the previous localIdx number from the panes parent using map, increment it, and get the next child.
                    int localIdx;
                    Pane parent;
                    int centurion = 0;
                    do {
                        parent = (Pane) parentPane.getParent();
                        // System.out.println("incrementing to next parent. parent: " + parent.getClass().getName());
                        parentPane = parent;
                        // System.out.println("line 181");
                        localIdx = map.get(parent);
                        // System.out.println("line 183");
                        localIdx++;
                        centurion++;
                        // System.out.println("centurion: " + centurion);
                    } while (localIdx >= parent.getChildren().size() && centurion < 15);
                    // recurse
                    // System.out.println("end of climb to Great Grand Parents, recursing from: " + parent.getClass().getName());
                    findNodeInPaneGraph(findMe, parent.getChildren().get(localIdx), parent, localIdx );
                }
                    //thread.stop();
                //}
            } catch (IndexOutOfBoundsException e) {
                // System.out.println("index out of bounds");
                returnNode = null;
                // this.thread.stop();
                /* should be caught by next round */
            }
        }

        //System.out.println("should never get here. returning null");
        return returnNode;
    }

    /**
     * returns the child of the pane at the idx
     * provided in the parameters
     * @param pane
     * @param idx
     * @return if exists, returns a pane else returns null
     */
    private Node getChildAtIDX(Pane pane, int idx) {

        if(idx < pane.getChildren().size()) {
            return pane.getChildren().get(idx);
        }
        return null;
    }


    /**
     * Prints out a representation of the Graph Structure
     * @param num
     * @param pane
     */
    void printChildren(int num, Pane pane) {
        System.out.println("in printChildren: ");

        if(pane != null && num > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(pane.getClass().getName() + " |   ->");
            for (int i = 0; i < num; i++) {
                String str = pane.getChildren().get(i).getClass().getName();
                String c = " contents size: " + ((Pane) pane.getChildren().get(i)).getChildren().size();
                sb.append(str + c + " | ");
            }
    
            System.out.println("child nodes: " + sb.toString());
        }
    }






    /* *** Inner Class Node *** **/

    private class PaneNode extends Pane {

        // This nodes index in it's parent pane.
        // ie pane.getChildren.get(index)
        private int idx;
        // Node can be a pane, textArea, ImageView or other.
        private Node node;


        public PaneNode(Node node) {

            super();
            this.idx = 0;
        }

        public int getNum() {
            return this.idx;
        }

        public void setNum(int n) {
            this.idx = n;
        }
    }

}


