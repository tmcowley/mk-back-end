package tmcowley.appserver.objects;

import java.util.List;
import java.util.ArrayList;
// for set sorting
import java.util.Collections;

/**
 * Store a pair of keys
 * 
 * e.g. (q, p) is a key-pair
 */
public class KeyPair {

    public Key key1;
    public Key key2;

    public KeyPair(Key key1, Key key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    public Key getOther(Key key) {

        if (key == null) {
            return null;
        }

        if (key.equals(key1)) {
            return key2;
        }

        if (key.equals(key2)) {
            return key1;
        }

        return null;
    }

    public String toString() {
        return ("(" + this.key1.character + ", " + this.key2.character + ")");
    }

    /**
     * Check if this equals the other Object
     * 
     * Two KeyPairs are equal if they contain the same two keys
     * 
     * @param other other object to be compared to this
     * @return equality state
     */
    @Override
    public boolean equals(Object other) {

        // ensure other key-pair is not null
        if (other == null) {
            return false;
        }

        // ensure 'other' Object is a Key
        if (!(other instanceof KeyPair)) {
            return false;
        }

        // case other object to a KeyPair
        KeyPair otherKeyPair = (KeyPair) other;

        // create a key set for more clear comparison
        List<Key> thisKeySet = new ArrayList<>(2);
        thisKeySet.add(this.key1);
        thisKeySet.add(this.key2);
        Collections.sort(thisKeySet, new SortKeys());

        // create a key set for more clear comparison
        List<Key> otherKeySet = new ArrayList<>(2);
        otherKeySet.add(otherKeyPair.key1);
        otherKeySet.add(otherKeyPair.key2);
        Collections.sort(otherKeySet, new SortKeys());

        if (thisKeySet.equals(otherKeySet)) {
            // keys match
            return true;
        }

        // keys do not match
        return false;
    }
}
