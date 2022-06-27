package fmtree;

/**
 * AVLTree GUI is specific to the use of integers in an AVLTree. To adopt to
 * a different type change the target type. Other adoptions may be necessary.
 * Displays a GUI of an AVLTree with nodes containing node data and the balance.
 * Balance and node data rely on the toString method of the AVLTree.
 * Extends <E Comparable<E>> and Application
 */

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class AVLTreeGUI<E extends Comparable<E>> { //extends Application {
/* Not used    
    /**
     * Start method: Starts the Old_AVLTree GUI
     * @param primaryStage 
     */
/*
    @Override 
    public void start(Stage primaryStage) {
        //AVLTree<E> avlTree = new AVLTree<>(); 
        FMTWalker walker = new FMTWalker();

        BorderPane pane = new BorderPane();
        AVLTreePane avltPane = new AVLTreePane(walker); 
        avltPane.setStyle("-fx-background-color: grey;");
        pane.setCenter(avltPane);

        TextField txtField = new TextField();
        txtField.setPrefColumnCount(4);
        txtField.setAlignment(Pos.BASELINE_RIGHT);
        Button btInsert = new Button("Insert");
        Button btDelete = new Button("Delete");
        Button insert10 = new Button("Insert 10");
        insert10.setStyle("-fx-text-fill: blue");
        HBox hBox = new HBox(5);
        hBox.setStyle("-fx-background-color: tomato;");
                
        hBox.getChildren().addAll(new Label("Enter a key: "), txtField,
                 btInsert, btDelete, insert10);
        hBox.setAlignment(Pos.CENTER);
        pane.setBottom(hBox);
        
        insert10.setOnAction(e -> {
            
            insert10.setDisable(true);
            // Create an imbalance
            Integer[] numbers = {50, 25, 75, 10, 90, 30, 70};
            
            for(int i = 0; i < numbers.length; i++) {
                walker.add((E) numbers[i]);
                avltPane.displayTree();
            }
            avltPane.setMessage( numbers.length +  " numbers added to the tree");
        });

        btInsert.setOnAction(e -> {
            Integer target = Integer.parseInt(txtField.getText());
            if (walker.find((E)target) != null) { 
                avltPane.displayTree();
                avltPane.setMessage(target + " is already in the tree");
            } else {
                walker.add((E)target); 
                avltPane.displayTree();
                avltPane.setMessage(target + " is inserted in the tree");
            }
        });

        btDelete.setOnAction(e -> {
            Integer target = Integer.parseInt(txtField.getText());
            if (walker.find((E)target) == null) { 
                avltPane.displayTree();
                avltPane.setMessage(target + " is not in the tree");
            } else {
                walker.delete((E)target); 
                avltPane.displayTree();
                avltPane.setMessage(target + " is deleted from the tree");
            }
        });

        
        Scene scene = new Scene(pane, 450, 250);
        primaryStage.setTitle("AVLTree"); 
        primaryStage.setScene(scene); 
        primaryStage.show(); 
    }
    
    /**
     * FlashMonkeyMain method. For use in some IDE's
     * @param args 
     */
/*
    public static void main(String[] args) {
        launch(args);
    }
*/
}
