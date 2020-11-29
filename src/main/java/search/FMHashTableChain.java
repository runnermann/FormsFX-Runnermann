package search;

/**
 * FMHashTableChain class extends HashTableChain and overrides the put method.
 * The put method for this class puts under the following conditions. If a Key
 * is already present it adds the index of the currentCard. If no key is present
 * in the table, it inserts a value. 

 * @author Lowell Stadelman
 */
// ***** IMPORTS *****
import java.util.Map;
import java.util.LinkedList;
import fmhashtablechain.*;
//import search.Search;

public class FMHashTableChain<K, V> extends HashTableChain<K, V> {
    
    
    /**
     * Constructor creates the initial array to store the LinkedLists in.
     */
    public FMHashTableChain() {
        super();  
    }
    
    /** 
     * 
     * put Method for class FMHashTableChain inserts a Term into the hash table
     * chain. If the term already exists, it adds this term.indicies to the 
     * indicies array of the existing Term.
     * post-condition This key-value pair is inserted in the
     * table and numKeys is incremented. If the key/(term.word) is already
     * in the table, term.indicies is added to the existing Term in the table.
     * value and numkeys is not changed.
     * @param key The key of the item being inserted
     * @param value The Term for this key
     * @return The old value associated with this key if 
     * found; otherwise, null
     */
    
    @Override
    public V put(K key, V value) {
        Search.Term st = new Search.Term();
        int index = key.hashCode() % table.length; 
        //System.out.println("index: " + index);
        
        if(index < 0) {
            index += table.length;    
        }
        if(table[index] == null) {
            // create new linked list at the table[index]
            table[index] = new LinkedList<>();   
        } 
        // if LinkedList exists at the index
        // search the list at the table [index] to find the key
        for(Entry<K, V> nextItem : table[index]) {
            // if the key already exists, replace the old value 
            if(nextItem.getKey().equals(key)) {
                V oldValue = nextItem.getValue();
                Search.Term currentTerm = (Search.Term) nextItem.getValue();
                Search.Term inputTerm = (Search.Term) value; 
                currentTerm.indices.add(inputTerm.indices.get(0));                
                return oldValue;
            }
        }
        // assert: key is not in the table, add new item
        table[index].addFirst(new SimpleEntry<>(key, value));
        numKeys++;
        
        if(numKeys > (LOAD_THRESHOLD * table.length)) {
            rehash();    
        }
        return null;
    }
    
}
