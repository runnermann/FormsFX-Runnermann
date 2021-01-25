package fmhashtablechain;


import fileops.MediaSyncObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class StateHashTable<K extends String, V extends MediaSyncObj> extends HashTableChain<K,V> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StateHashTable.class);
	
	public StateHashTable() {
		super();
	}
	
	/**
	 * <p> Uses @code(<String, Integer>) for key, map</p>
	 * <p>Sets the K,V pair into the map. If there is a collision at
	 * K then ADDs the value to the existing value. </p>
	 * <p>The resulting integer after three passes will determine
	 * the state change for the key between remote and local
	 * locations.</p>
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public V put(K key, V value) {
		
		LOGGER.debug("StateHashTable() called:");
		
		int index = key.hashCode() & (table.length -1);
		
		if(index < 0) {
			index += table.length;
		}
		
		if(table[index] == null) {
			table[index] = new LinkedList<>();
		}
		// if LinkedList exists at the index
		// search the list at the table [index] to find the key
		for(Entry<K, V> entry : table[index]) {
			// check if String fileName already
			// exists in the table
			if (entry.getKey().equals(key)) {
				MediaSyncObj newVal = entry.getValue();// + (MediaSyncObj) value;
				newVal.setState( newVal.getState() + value.getState());
				entry.setValue( (V) newVal );
				
				LOGGER.debug("valueName: " + value.getFileName() + ", state value: " + newVal.getState());;
				
				return entry.getValue();
			}
		}
		// key is not in the table, add new item
		table[index].addFirst(new SimpleEntry<>(key, value));
		numKeys++;
		
		LOGGER.debug("valueName: " + value.getFileName() + " state: " + value.getState());
		
		if(numKeys > (LOAD_THRESHOLD * table.length)) {
			rehash();
		}
		
		
		
		return null;
	}
}
