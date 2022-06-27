package flashmonkey;

//import javafx.geometry.Insets;

import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
//import org.scilab.forge.jlatexmath.*;

import java.awt.*;

/**
 * Used by math-cards. Experimental in nature.
 * Provides spcial characters and symbols for m
 * math functions.
 *
 * @author Lowell Stadelman
 */
public class FMLaTeXControl {

    /*

    private static final String DEFAULT_FORMULA = "";
    private static final Color DEFAULT_BG = Color.TRANSPARENT;
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final double DEFAULT_FONT_SIZE = 12;// Font.getDefault().getSize();

    private String formula;
    private double fontSize;
    private Color textColor;
    private Color bgColor;
    private TeXIcon teXIcon;
    private Canvas canvas;
    private Insets insets;

    private Pane pane;

    Box box;


    // CONSTRUCTORS

    public void FMLatexControl() {

        this.formula = DEFAULT_FORMULA;
        this.bgColor = DEFAULT_BG;
        this.textColor = DEFAULT_COLOR;
        this.fontSize = DEFAULT_FONT_SIZE;
    }

    /**
     * Constructor. Sets the String formula to the parameter
     * @param formula
     */
/*    public FMLaTeXControl(String formula) {
        this.FMLatexControl();
        this.formula = formula;
    }

    // SETTERS


    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    // GETTERS

    public String getFormula() {
        return formula;
    }

    public Pane getIconPane() {
        return pane;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void updateTeXIcon() {
        try {

            TeXFormula teXFormula = new TeXFormula(formula);
            teXIcon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) fontSize);
            teXIcon.setInsets(new Insets(1, 1, 1, 1));
        }
        catch (ParseException p) {
            //getSkinnable().setFormula("\\text{Invalid Formula}");
            this.formula = DEFAULT_FORMULA;
            TeXFormula teXFormula = new TeXFormula(formula);
            teXIcon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) DEFAULT_FONT_SIZE);
            teXIcon.setInsets(new Insets(1, 1, 1, 1));
        }
    }


    public void updateCanvas() {


        if (null == canvas) {
            canvas = new Canvas();
            pane.getChildren().add(canvas);
        }

        paintInCanvas(canvas, 0, 0, textColor, bgColor);
        canvas.setWidth(teXIcon.getIconWidth());
        canvas.setHeight(teXIcon.getIconHeight());
    }

    public void paintInCanvas(Canvas canvas, int x, int y, Paint fg, Paint bg) {
        GraphicsContext g2 = canvas.getGraphicsContext2D();
        g2.save();
        g2.scale(fontSize, fontSize);

        g2.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        if (null != bg) {
            g2.setFill(bg);
            g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        }

        //if(null != fg) {
            //box.foreground = DEFAULT_COLOR;
        //}

        if (null == fg) {
            g2.setFill(DEFAULT_COLOR);
        } else {
            g2.setFill(fg);
        }

        box.draw(g2, (x + 0.18f * fontSize) / fontSize, (y + 0.18f * fontSize) / fontSize + box.getHeight());
        g2.restore();
    }
    */
      /**
       @Override protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
       if (invalid) {
       updateCanvas();
       invalid = false;
       }
       layoutInArea(canvas, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER);
       }
       **/


}
