package fmhashtablechain;

import flashmonkey.*;

/**
 * An interface for HashMap
 *
 * @author Koffman and Wolfgang
 */
public interface KWHashMap<K, V> {

      /**
       * return the key
       *
       * @param key Object
       * @return return the key
       */
      V get(Object key);

      /**
       * puts the K key and V value into a set
       *
       * @param key
       * @param value
       * @return the value
       */
      V put(K key, V value);

      /**
       * removes the item in the argument
       *
       * @param key
       * @return the value removed
       */
      V remove(Object key);

      /**
       * returns the size
       *
       * @return int size
       */
      int size();

      /**
       * returns true if the list is emtpy
       *
       * @return treu if the lsit is emtpy, false otherwise
       */
      boolean isEmpty();

}
