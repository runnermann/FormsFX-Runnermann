package forms;

public interface Descriptor<E extends FormData> {
	
	/**
	 * Compares the DB version with local version using
	 * date. Sets the data to the most recent version.
	 */
	public void setMostRecent();
	
	/**
	 * Helper method to setMostRecent
	 * @return the timeStamp in millis of a local meta file
	 * 	 * if exists. If not returns 0.
	 */
	public long getLocalDataDate();
	
	public void setToLocalData();
	
	public void setToRemoteData();
	
	//public String[] parseResponse(String[] response);
	
	public void setProperties(final E fm);
	
	/**
	 * Sets the defaultProperties
	 */
	public void setProperitesDefault();
	
	
	
	/**
	 * Clears the form and sets
	 * to default values.
	 */
	public void clear();
	
	
	
}
