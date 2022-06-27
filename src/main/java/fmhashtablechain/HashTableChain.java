package fmhashtablechain;

// ***** IMPORTS *****

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>HashTableChain class stores, retrieves, and deletes objects at an expected
 * rate of O(1). Some situations may take longer if the table needs to rehash. </p>
 *
 * <p>INPUT:
 * HashTableChain takes an input from the EncryptedUser.EncryptedUser which is stored as a value. The
 * EncryptedUser.EncryptedUser of this class must determine a key that is as unique as possible.
 * Preferably an int or a String. The key is hashed to determine as unique as
 * possible of an index(to prevent collisions) and attempts to store it at that
 * index in an array. Because collisions are expected, each index of the array,
 * if the index is used, contains a non-sorted LinkedList. As the array reaches
 * its threshold load-factor, the data-structure(HashTableChain) will rehash.
 * rehashing in this Data-Structure doubles its size. </p>
 *
 * <p>SEARCH/RETREIVE:
 * HashTableChain takes an input from the EncryptedUser.EncryptedUser which becomes the
 * key. The key is hashed to determine the index where it might be stored.
 * The program checks the index at the hashed key. If the index is used and is
 * not null, the program checks the LinkedList elements for that key. If there
 * is nothing stored at that index. retrieve will return null. </p>
 *
 * <p>OUTPUT:
 * REMOVE: Searches the index provided by the hashed key. If the key is stored
 * in the non-sorted linked list, it returns the value and deletes it from the
 * LinkedList. </p>
 *
 * @author Lowell Stadelman
 */
public class HashTableChain<K, V> extends AbstractMap<K, V>
    implements KWHashMap<K, V> {

      /**
       * An array of LinkedLists used to store values
       **/
      //@todo make table private , line 42 HashTableChain()
      protected LinkedList<Entry<K, V>>[] table;
      /**
       * The total number of keys stored in the table including the linked-list
       */
      protected int numKeys;
      /**
       * The capacity of the array a prime number
       **/
      public static final int CAPACITY = 21;
      /**
       * The maximum load factor
       **/
      protected static final double LOAD_THRESHOLD = .8;

      /**
       * The object to be used as a key
       **/
      protected K key;
      /**
       * The object that is the value to be stored
       **/
      protected V value;

      // LOGGER
      private static final Logger LOGGER = LoggerFactory.getLogger(HashTableChain.class);


      /**
       * Inner Class Entry Object
       **/
      Entry<K, V> entry = new SimpleEntry<>(key, value);

      // *** Constructor ***

      /**
       * Constructor creates the initial array to store the LinkedLists in.
       */
      public HashTableChain() {
            table = new LinkedList[CAPACITY];

            LOGGER.info("HashTableChain constructor called");
      }

      /**
       * get Method for class HashTableChain
       *
       * @param key The key that is sought
       * @return The value associated with this key if found
       * Otherwise null.
       */
      @Override
      public V get(Object key) {
            int index = key.hashCode() % table.length;
            if (index < 0) {
                  index += table.length;
            }
            if (table[index] == null) {
                  return null; // Key not in the table!
            }
            //Search the list at table[index] to find the key
            for (Entry<K, V> nextItem : table[index]) {
                  if (nextItem.getKey().equals(key)) {
                        return nextItem.getValue();
                  }
            }
            // assert: key is not in the table.
            return null;
      }

      /**
       * A key-value pair is inserted in the
       * table and numKeys is incremented. If the key is already
       * in the table, its value is changed to the argument
       * value and numkeys is not changed.
       *
       * @param key   The key of the item being inserted
       * @param value The value for this key
       * @return The old value associated with this key if
       * found; otherwise, null
       */
      @Override
      public V put(K key, V value) {

            int index = key.hashCode() % table.length;

            //System.out.println("index: " + index);

            if (index < 0) {
                  index += table.length;
            }
            if (table[index] == null) {
                  // create new linked list at the table[index]
                  table[index] = new LinkedList<>();
            }
            // if LinkedList exists at the index
            // search the list at the table [index] to find the key
            for (Entry<K, V> nextItem : table[index]) {
                  // if the key already exists, replace the old value
                  if (nextItem.getKey().equals(key)) {
                        V oldValue = nextItem.getValue();
                        nextItem.setValue(value);
                        return oldValue;
                  }
            }
            // assert: key is not in the table, add new item
            table[index].addFirst(new SimpleEntry<>(key, value));
            numKeys++;

            if (numKeys > (LOAD_THRESHOLD * table.length)) {
                  rehash();
            }
            return null;
      }

      /**
       * Method remove for the class HashTableChain
       *
       * @param key The object to be removed
       * @return Returns the old value if it is removed else
       * returns null
       */
      @Override
      public V remove(Object key) {

            // set the index of the key
            int index = (key.hashCode() % table.length);

            // if index is negitive add index.length
            if (index < 0) {
                  index += table.length;
            }
            // if index is null key is not in the table
            if (table[index] == null) {
                  return null;
            }
            // Search the list at table index to find the key
            for (Iterator<Entry<K, V>> it = table[index].iterator(); it.hasNext(); ) {

                  Entry<K, V> entry = it.next();

                  if (entry.getKey().equals(key)) {
                        V oldValue = entry.getValue();
                        entry.setValue(null);
                        return oldValue;
                  }
            }
            return null;
      }

      /**
       * returns a map set
       *
       * @return
       */
      @Override
      public java.util.Set<Entry<K, V>> entrySet() {
            return new EntrySet();
      }


      // **** OTHER METHODS ****

      /**
       * toString method. Returns the table printed in rows and columns
       *
       * @return Returns the table printed in rows and columns
       */
      @Override
      public String toString() {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < table.length; i++) {
                  if (table[i] != null) {
                        sb.append(i + ") ");
                        for (int j = 0; j < table[i].size(); j++) {

                              if (table[i].get(j) != null) {

                                    sb.append(table[i].get(j)).append(", ");
                              } else {
                                    sb.append(" empty ");
                              }
                        }
                        sb.append("\n");
                  } else {
                        sb.append(i + ") \n");
                  }
            }
            return sb.toString();
      }

      /**
       * Returns the number of entries in the Map
       *
       * @return
       */
      @Override
      public int size() {
            return super.size();
      }


      /**
       * Rehash Expands the table size when loadFactor exceeds
       * LOAD_THRESHOLD
       * Expects the size of the table is a doubled and is an
       * odd integer. Each entry from the original
       * table is reinserted into the expanded table.
       */
      //@Override
      public void rehash() {

            // Double the capacity
            int newCapacity = 2 * table.length + 1;

            // Save the old referance
            LinkedList<Entry<K, V>>[] oldTable = table;
            table = new LinkedList[newCapacity];
            numKeys = 0;

            // Reinsert all items in oldTable into expanded table.

            for (int i = 0; i < oldTable.length; i++) {

                  if (oldTable[i] != null) {
                        // do not copy an Entry if value has been deleted
                        for (int j = 0; j < oldTable[i].size(); j++) {

                              K jKey = oldTable[i].get(j).getKey();
                              V jValue = oldTable[i].get(j).getValue();

                              put(jKey, jValue);

                        }
                  }
            }
      }


      /**
       * Inner Class EntrySet to implement the set
       * Contains all the necessary functions to access the key and value of a table
       * element
       */
      private class EntrySet extends java.util.AbstractSet<Entry<K, V>> {

            // @todo make inner-class EntrySet in HashTableChain static, or a seperate class.

            /**
             * return the size of the set
             */
            @Override
            public int size() {
                  return numKeys;
            }

            /**
             * return the iterator over the set
             */
            @Override
            public Iterator<Entry<K, V>> iterator() {
                  return new SetIterator();

            }


      } // END inner class EntrySet

      /**
       * Inner Class to implement the set iterator.
       */

      private class SetIterator implements Iterator<Entry<K, V>> {

            // @todo make inner-class SetIterator in HashTableChain static or a seperate class

            int index = 0;
            Entry<K, V> lastItemReturned;
            Iterator<Entry<K, V>> localIterator = null;


            /**
             * If there is another item in the LinkedList returns true
             *
             * @return True if there is another item in the linked list. Otherwise
             * false.
             */
            @Override
            public boolean hasNext() {
                  if (localIterator != null) {

                        if (localIterator.hasNext()) {
                              return true;
                        } else {
                              localIterator = null;
                              index++;
                        }
                  }
                  while (index < table.length && table[index] == null) {
                        index++;
                  }
                  if (index == table.length) {
                        return false;
                  }
                  localIterator = table[index].iterator();

                  return localIterator.hasNext();

            }

            /**
             * Returns the next item in the iterator
             *
             * @return Map.Entry next item in the iterator
             */
            @Override
            public Entry<K, V> next() {
                  lastItemReturned = localIterator.next();
                  return lastItemReturned;
            }


            /**
             * Removes the last item returned by the iterator
             */
            @Override
            public void remove() {
                  if (lastItemReturned != null) {
                        localIterator.remove();
                        lastItemReturned = null;
                  }
            }
      } // END inner class SetIterator

}
