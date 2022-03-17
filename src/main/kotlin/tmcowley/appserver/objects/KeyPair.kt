package tmcowley.appserver.objects

final data class KeyPair(val leftKey: Key, val rightKey: Key) {

    constructor(leftChar: Char, rightChar: Char) : this(Key(leftChar), Key(rightChar)) {}

    // equals, hashCode, toString all defined

    /** string representaiton is pair of keys as strings, e.g. "(q, p)" */
    override fun toString(): String {
        return ("(" + this.leftKey.toString() + ", " + this.rightKey.toString() + ")")
    }
}
