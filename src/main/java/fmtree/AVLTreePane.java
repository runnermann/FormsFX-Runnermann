package fmtree;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import flashmonkey.FlashCardMM;
import flashmonkey.ReadFlash;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class AVLTreePane<E extends Comparable<E>> extends Pane {
/*
    //private AVLTree<E> avlTree = new AVLTree<>();
    protected FMTWalker walker = FMTWalker.getInstance();
    private static final double UNSELECTED = 20;
    private static final double SELECTED = 27;
    private double vGap = 50; 

    public AVLTreePane(FMTWalker<E> wlkr) {
        this.walker = wlkr;
        setMessage("Not even a seedling here");
    }

    public void setMessage(String msg) {
        getChildren().add(new Text(20, 20, msg));
    }

    public void displayTree() {
        this.getChildren().clear(); 
        if (walker.getData()!= null) {    
            displayTree( walker.root , getWidth() / 2, vGap, getWidth() / 4);
        }
    }

    private void displayTree(FMTWalker.Node node, double x, double y, 
            double hGap) {
        
        Circle circle;
                      
        if (node.left != null) {
            getChildren().add(new Line(x - hGap, y + vGap, x, y));
            displayTree(node.left, x - hGap, y + vGap, hGap / 2);
        }

        if (node.right != null) {
            getChildren().add(new Line(x + hGap, y + vGap, x, y));
            displayTree(node.right, x + hGap, y + vGap, hGap / 2);
        }

        if( node == ReadFlash.getCurrentNode())
        {
            circle = new Circle(x, y, SELECTED);
            circle.setStrokeWidth(3);
        }
        else
        {
            circle = new Circle(x, y, UNSELECTED);
            circle.setStrokeWidth(.5);
        }
        
        circle.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
                public void handle(MouseEvent event) 
                {
                    ReadFlash.setCurrentNode(node);
                    ReadFlash.setTreeCard(ReadFlash.getCurrentNode());
                    
                    
                }
            
        
        });
        
        circle.setFill(fillColor(node));
        circle.setStroke(lineColor(node));
        getChildren().addAll(circle, new Text(x - 4, y + 4, 
                node.toString() + " ")); 
        
    }
    
 */
    
    /** 
     * Sets the color of the line according to past history of the card.
     * Compares numRight with numSeen or in some cases with score. 
     * @param currentCard
     * @return 
     */
/*
    private Color lineColor(FMTWalker.Node node) {
        
        System.out.println(" *** In in lineColor status ***");
        FlashCard currentCard = (FlashCard) node.getData();
        int numSeen = currentCard.getNumSeen();
        int numRt = currentCard.getNumRight();
        
        if (node == ReadFlash.getCurrentNode())
        {
            return Color.web("#039ED3"); // glow blue
        }
        if (currentCard.getScore() > numSeen * 1.5)
        {
            return Color.web("#02420F"); // FM dark green
        }
        else if (currentCard.getScore() > numSeen )
        {
            return Color.web("#05D835"); // FM green  #188A07
        }
        else if (numRt < numSeen * 1.5)
        {
            return Color.web("#8D060A"); // FM dark red #8D060A
        }
        else if (numRt < numSeen)
        {
            return Color.web("#D80519"); // FM red
        }
        else
        {
            return Color.web("#747474"); // black
        }
    }
    
 */
    
    /**
     * Sets the color of the fill according to this sessions status
     * @param currentCard
     * @return Returns White if not seen, red if incorrect, and green if correct.
     */
  /*
    private Color fillColor(FMTWalker.Node node)
    //private Color fillColor(FlashCard currentCard)
    {
        System.out.println("in FillColor status");
        FlashCard currentCard = (FlashCard) node.getData();
        
        if (currentCard.getIsRight() == -1)
        {
            return Color.web("#D80519"); // FM red  
        }
        else if(currentCard.getIsRight() == 1)
        {
            return Color.web("#188A07"); // FM green
        }
        else if (currentCard.getQNumber() %10 != 0)
        {
            return Color.web("#D88F05"); // FM Orange #D88F05
        }
        else if (node == ReadFlash.getCurrentNode())
        {
            return Color.web("#F4F4F4"); // FM white #F4F4F4
        }
        else
        {
            return Color.web("#DADADA");  // non selected unvisited -FM off white #F6EB79
        }
    }
    
   */
    
}
