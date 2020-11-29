package draw.shapes;

import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;

import java.io.Serializable;

import static uicontrols.UIColors.convertColor;

public class FMPolygon extends GenericShape<FMPolygon> implements Serializable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FMPolygon.class);
	
	double[] points;
	
	protected double x1;
	protected double y1;
	
	// A shape knows its index in the
	// shape array
	private int shapeAryIdx;
	
	private double strokeWidth;
	private String strokeColor;
	private String fillColor;
	
	/**
	 * No args constructor
	 */
	public FMPolygon(){ /* empty */ }
	
	public FMPolygon(double[] points, double strokeW, String stroke, String fill, int index) {
		double[] pAry = new double[points.length];
		int i = 0;
		for(double thisDbl : this.points) {
			pAry[i] = thisDbl;
			i++;
		}
		this.points = pAry;
		x1 = pAry[0];
		y1 = pAry[1];
		this.strokeWidth = strokeW;
		this.strokeColor = stroke;
		this.fillColor = fill;
		this.shapeAryIdx = index;
		this.strokeColor = stroke;
	}
	
	public FMPolygon(double x1, double x2, double deltaX, double deltaY, int numPoints,
	                 double strokeW, double stroke, String fill, int index) {
		
	}
	
	@Override
	public void setPoints(ObservableList<Double> pts) {
		//if(pts.size() == size) {
			// ???????
		//}
	}
	
	@Override
	public void setX(double x1) {
		this.x1 = x1;
	}
	
	@Override
	public void setY(double y1) {
		this.y1 = y1;
	}
	
	@Override
	public void setShapeAryIdx(int i) {
		this.shapeAryIdx = i;
	}
	
	@Override
	public void setStrokeWidth(double strokeWd) {
		this.strokeWidth = strokeWd;
	}
	
	@Override
	public void setStrokeColor(String strokeClr) {
		this.strokeColor = strokeClr;
	}
	
	@Override
	public void setFillColor(String fillClr) {
		this.fillColor = fillClr;
	}
	
	@Override
	public double getX() {
		return this.x1;
	}
	
	@Override
	public double getY() {
		return this.y1;
	}
	
	@Override
	public int getShapeAryIdx() {
		return this.shapeAryIdx;
	}
	
	@Override
	public double getStrokeWidth() {
		return this.strokeWidth;
	}
	
	@Override
	public String getStrokeColor() {
		return this.strokeColor;
	}
	
	@Override
	public String getFillColor() {
		return this.fillColor;
	}
	
	@Override
	public Polygon getShape() {
		Polygon p = new Polygon(this.points);
		
		p.setStrokeWidth(this.strokeWidth);
		p.setStroke(convertColor(this.strokeColor));
		p.setFill(convertColor(this.fillColor));
		return p;
	}
	
	@Override
	public Polygon getScaledShape(double scaleY) {
		
		double[] pAry = new double[this.points.length];
		int i = 0;
		for(double d : this.points) {
			pAry[i] = d * scaleY;
			i++;
		}
		
		Polygon p = new Polygon(pAry);
		p.setStrokeWidth(this.getStrokeWidth() * scaleY);
		p.setStroke(convertColor(this.getStrokeColor()));
		p.setFill(convertColor(this.getFillColor()));
		return p;
	}
	
	@Override
	public GenericBuilder getBuilder(SectionEditor editor, boolean scaled) {
		DrawTools dt = DrawTools.getInstance();
		//scaled to be used later = scaled
		return new PolygonBuilder(
				this,
				dt.getCanvas(),
				dt.getGrapContext(),
				dt.getOverlayPane(),
				editor
		);
	}
	
	
	@Override
	public GenericBuilder getBuilder(SectionEditor editor) {
		DrawTools dt = DrawTools.getInstance();
		return new PolygonBuilder(
				this,
				dt.getCanvas(),
				dt.getGrapContext(),
				dt.getOverlayPane(),
				editor
		);
	}
	
	@Override
	public Canvas convertToCanvas(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		int size = this.points.length / 2;
		double[] xPoints = new double[size];
		double[] yPoints = new double[size];

		for(int i = 0; i < size; i++) {
			// x = odds
			xPoints[i] = points[i];
			// y = evens
			i++;
			yPoints[i] = points[i];
		}
		
		gc.strokePolygon(xPoints, yPoints, size);
		gc.setStroke(convertColor(this.getStrokeColor()));
		gc.setFill(convertColor(this.getFillColor()));
		gc.setLineWidth(this.getStrokeWidth());
		
		return canvas;
	}
	
	@Override
	public GenericShape convertFmCanvas(Canvas poly) {
		/*
		return new FMPolygon(
				poly.getLayoutX(),
				poly.getLayoutY(),
				poly.getWidth(),
				poly.getHeight(),
				poly.getGraphicsContext2D().getLineWidth(),
				poly.getGraphicsContext2D().getStroke().toString(),
				poly.getGraphicsContext2D().getFill().toString(),
				0);
		)
		 */
		return new FMPolygon();
	}
	
	@Override
	public boolean equals(Object other) {
		if(! other.getClass().getName().equals("FMPolygon")) {
			return false;
		}
		FMPolygon otherFMPoly = (FMPolygon) other;
		double[] othPoints = otherFMPoly.points;
		int i = 0;
		for(double pt : this.points) {
			if(pt != othPoints[i]) {
				return false;
			}
			i++;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = this.getClass().getName().hashCode();
		for(double p : points) {
			hash += p;
		}
		return hash;
	}
	
	/**
	 * @return Returns a deep copy of this object
	 */
	@Override
	public GenericShape clone() {
		return new FMPolygon(
				this.points,
				this.strokeWidth,
				this.strokeColor,
				this.fillColor,
				this.shapeAryIdx
		);
	}
	
	@Override
	public String toString() {
		return " this is a FMPolygon";
	}
	

}
