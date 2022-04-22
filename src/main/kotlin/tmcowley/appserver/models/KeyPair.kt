package tmcowley.appserver.models

/** 
 * a key-pair is an ordered pair of keys; 
 * Kotlin data class defines equals, hashCode, and toString
 */
data class KeyPair(val leftKey: Key, val rightKey: Key) {

    /** overloaded constructor to support key-pair creation by characters, not keys */
    constructor(leftChar: Char, rightChar: Char) : this(Key(leftChar), Key(rightChar))

    /** string representation is the ordered pair of keys, e.g. "(q, p)" */
    override fun toString(): String {
        return ("(" + this.leftKey.toString() + ", " + this.rightKey.toString() + ")")
    }

    /** check if the key-pair contains the given key */
    fun contains(key: Key): Boolean {
        return (key == leftKey || key == rightKey)
    }
}
