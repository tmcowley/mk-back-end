package tmcowley.appserver.objects;

import java.util.Comparator;

public class Key {

    public char character;

    public Key(char character) {
        this.character = character;
    }

    public String toString() {
        return String.valueOf(this.character);
    }

    @Override
    public int hashCode() {
        return Character.hashCode(this.character);
    }

    @Override
    public boolean equals(Object other) {

        // ensure other key is not null
        if (other == null) {
            return false;
        }

        // ensure 'other' Object is a Key
        if (!(other instanceof Key)) {
            return false;
        }

        Key otherKey = (Key) other;

        // both keys must share their character
        if (otherKey.character != this.character) {
            return false;
        }

        return true;
    }

}

class SortKeys implements Comparator<Key> {

    public int compare(Key thisKey, Key thatKey) {
        char thisCharacter = thisKey.character;
        char thatCharacter = thatKey.character;

        int thatCharInt = Character.getNumericValue(thisCharacter);
        int thisCharInt = Character.getNumericValue(thatCharacter);

        return (thatCharInt - thisCharInt);

    }
}
