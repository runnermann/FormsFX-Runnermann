package draw;

/**
 * Class used to pass values from SnapShot to DrawTools. 
 */
public class DrawObj {
	
	private int minX;
	private int minY;
	private int deltaX;
	private int deltaY;
	private String fullPathName;
	private String imageName;
	
	/**
	 * Default constructor. Sets all values to a default.
	 */
	public DrawObj() { /*default constructor*/ }
	
	/**
	 * Contains demensions set in SnapShot to be used by
	 * DrawTools.
	 * @param minX
	 * @param minY
	 * @param deltaX
	 * @param deltaY
	 */
	public void setDems(int minX, int minY, int deltaX, int deltaY) {
		this.minX = minX;
		this.minY = minY;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}
	
	/**
	 * Sets the full path name
	 * @param fullPathName
	 */
	public void setFullPathName(String fullPathName) {
		this.fullPathName = fullPathName;
	}
	
	public void setImageName(String imgName) { imageName = imgName; }
	
	public String getImageName() { return imageName; }
	
	public int getMinX() {
		return minX;
	}
	
	public int getMinY() {
		return minY;
	}
	
	public int getDeltaX() {
		return deltaX;
	}
	
	public int getDeltaY() {
		return deltaY;
	}
	
	public String getFullPathName() {
		return fullPathName;
	}
	
	public void clearDrawObj() {
		minX = 0;
		minY = 0;
		deltaX = 0;
		deltaY = 0;
		fullPathName = "";
	}
}
