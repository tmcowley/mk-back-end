package tmcowley.appserver.objects;

import tmcowley.appserver.objects.Key;

data class KeyPair(val leftKey: Key, val rightKey: Key) {

    // equals, hashCode, toString all defined

    fun getOtherKey(key: Key?): Key? {

        if (key == null) {
            return null
        }

        if (key.equals(this.leftKey)) {
            return this.rightKey;
        }

        if (key.equals(this.rightKey)) {
            return this.leftKey;
        }

        return null;
    }

    override fun toString(): String {
        return ("(" + this.leftKey.toString() + ", " + this.rightKey.toString() + ")");
    }
}