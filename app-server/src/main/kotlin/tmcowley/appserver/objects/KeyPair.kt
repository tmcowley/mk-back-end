package tmcowley.appserver.objects;

import tmcowley.appserver.objects.Key;

data class KeyPair(val key1: Key, val key2: Key) {

    // equals, hashCode, toString all defined

    fun getOtherKey(key: Key?): Key? {

        if (key == null) {
            return null
        }

        if (key.equals(this.key1)) {
            return this.key2;
        }

        if (key.equals(this.key2)) {
            return this.key1;
        }

        return null;
    }

    override fun toString(): String {
        return ("(" + this.key1.toString() + ", " + this.key2.toString() + ")");
    }
}