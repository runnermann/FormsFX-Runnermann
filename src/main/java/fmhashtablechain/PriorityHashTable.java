package fmhashtablechain;

import fileops.LinkObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// ***** IMPORTS *****
import java.util.LinkedList;

/**
 * This class is used to synchronize FlashDecks between the local device and the cloud.
 * during a put() operation, if A key/ (fileName) already exists, a comparison will
 * be made by the date to determine if the current key is kept, or replaced.
 */
public class PriorityHashTable<K extends String, V extends LinkObj> extends HashTableChain<K,V> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PriorityHashTable.class);
	
	public PriorityHashTable() {
		super();
	}
	
	/**
	 * <p> Uses {@code(<String, LinkObj<K,V>} for pair</p>
	 * <p>Sets the K,V pair into the map. If there is a collision at
	 * K then compares the dateInMilli's between LinkObj's and keeps
	 * the most recent. </p>
	 * @param key Expects a String . Use the file name
	 * @param value Expects a LinkObj
	 * @return The unused value associated with this key if
	 * found and replaced; otherwise, null
	 */
	@Override
	public V put(K key, V value) {
		
		LOGGER.info("PriorityHashTable.put() called: ");
	//	System.out.println("value.getCLoudLink.getName: " + ((LinkObj) value).getCloudLink().getName());
		
		int index = key.hashCode() & (table.length - 1);
		
		if(index < 0) {
			index += table.length;
		}
		if(table[index] == null) {
			// create new linked list at the table[index]
			table[index] = new LinkedList<>();
		}
		// if LinkedList exists at the index
		// search the list at the table [index] to find the key
		// LinkedList<Map.Entry<K,V>> bucket = table[index];
		for(Entry<K, V> entry : table[index]) {
			
			// check if String fileName already
			// exists in the table
			if (entry.getKey().equals(key)) {
				//LinkObj pre = entry.getValue();
				LOGGER.debug("\t key " + key + ": local file dateInMillis: " + entry.getValue().getTimeInMillis() + " remoteFile dateInMillis: " + value.getTimeInMillis());
				if (value.compare(entry.getValue(), value) < 0) {
					LOGGER.debug("replacing with remote " + key);
					V oldValue = entry.getValue();
					entry.setValue( value);
					return oldValue;
				}
				LOGGER.debug("\tremote key " + key + " is older than the key " + entry.getKey() + " in the table.");
				return null;
			}
		}
		// key is not in the table, add new item
		LOGGER.debug("\tAdding key: " + key);
		table[index].addFirst(new SimpleEntry<>(key, value));
		numKeys++;
		
		if(numKeys > (LOAD_THRESHOLD * table.length)) {
			rehash();
		}
		
		return null;
	}
}
