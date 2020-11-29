package fileops;

/**
 * Provides an object that contains the
 * mediaName, the mediaURL, and it's sync state.
 */
public class MediaSyncObj<T extends MediaSyncObj> implements Comparable<T>{
	
	private String fileName;
	private char type;
	private int state;
	
	public MediaSyncObj(String fileName, char type, int stateNum) {
		this.fileName = fileName;
		this.type = type;
		this.state = stateNum;
	}
	
	public MediaSyncObj(CloudLink cl) {
		this.fileName = cl.getName();
		this.type = 'm';
		this.state = 4;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() { return this.state;}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public char getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object other) {
		MediaSyncObj s = (T) other;
		return this.fileName.equals(s);
	}
	
	@Override
	public int hashCode() {
		return this.fileName.hashCode();
	}
	
	@Override
	public int compareTo(T other) {
		return this.fileName.compareTo(other.getFileName());
	}
}
