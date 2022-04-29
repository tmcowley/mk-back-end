package tmcowley.appserver.models

/** 
 * a key-pair is an ordered pair of keys; 
 * Kotlin data class defines equals, hashCode, and toString
 */
data class KeyPair(val leftKey: Key, val rightKey: Key) : Iterator<Key> {

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

    /** KeyPair as list */
    fun toList(): List<Key> = listOf(
        leftKey, rightKey
    )

    /** KeyPair as set */
    fun toSet(): Set<Key> = setOf(
        leftKey, rightKey
    )

    // used to iterate over the KeyPair instance
    private var asListIterator = this.toList().iterator()

    /** check if there is another value when iterating */
    override fun hasNext(): Boolean {
        val hasNext = this.asListIterator.hasNext()

        // reset iterator
        if (!hasNext) asListIterator = this.toList().iterator()

        return hasNext
    }

    /** get the next character when iterating */
    override fun next(): Key = this.asListIterator.next()
}
