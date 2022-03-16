package tmcowley.appserver.objects

data class KeyPair(val leftKey: Key, val rightKey: Key) {

    constructor(leftChar: Char, rightChar: Char) : this(Key(leftChar), Key(rightChar)) {}

    fun getOtherKey(key: Key?): Key? {

        if (key == null) return null

        if (key.equals(this.leftKey)) {
            return this.rightKey
        }

        if (key.equals(this.rightKey)) {
            return this.leftKey
        }

        return null
    }

    // equals, hashCode, toString all defined

    override fun toString(): String {
        return ("(" + this.leftKey.toString() + ", " + this.rightKey.toString() + ")")
    }
}
