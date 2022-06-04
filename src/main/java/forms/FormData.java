package forms;

import java.io.FileNotFoundException;
import java.util.HashMap;

public interface FormData {
	
	
	public void init();
	
	/**
	 * Sets this object form the DataAry from this object;
	 * @return If the dataAry is missing the Description or
	 * the lastDate is 0 returns false signifying a failure.
	 */
	public boolean set();
	
	/**
	 * Sets this instances variables from the DataAry
	 * @param map
	 */
	public void set(HashMap<String, String> map);
	
	/**
	 * Sets this class instance to null
	 */
	public void close();
	
	/**
	 * Sets this objects metaDataAry to the array in the param.
	 * @param dataMap
	 */
	public boolean setDataMap(HashMap<String, String> dataMap);
	
	
	/**
	 * Creates a String[] of data of the current user.
	 */
	public void updateDataMap();
	
	/**
	 * Returns the deckMetaData array created by this class.
	 * @return
	 */
	public HashMap<String, String> getDataMap();
	
	/**
	 * Retrieves deckMetaData from file if available
	 * @return returns The deck metaData
	 */
/*	public String[] getData(String filePathName) throws FileNotFoundException; */
	
	/**
	 * Sets this decks metadata stored in a file to
	 * this object
	 */
/*	public boolean setDataAryFmFile(); */
	
	
	/**
	 * The last timeStamp from when this was updated
	 * @return
	 */
	public long getLastDate();
	
	
	
	
}
