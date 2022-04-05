package tmcowley.appserver.models

data class KeyPair(val leftKey: Key, val rightKey: Key) {

    constructor(leftChar: Char, rightChar: Char) : this(Key(leftChar), Key(rightChar))

    // equals, hashCode, toString all defined

    /** string representation is the pair of keys, e.g. "(q, p)" */
    override fun toString(): String {
        return ("(" + this.leftKey.toString() + ", " + this.rightKey.toString() + ")")
    }

    fun contains(key: Key): Boolean {
        return (key == leftKey || key == rightKey)
    }
}
