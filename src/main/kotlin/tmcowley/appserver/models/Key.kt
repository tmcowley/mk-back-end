package tmcowley.appserver.models

data class Key(val character: Char) {

    // equals, hashCode, toString all defined

    /** string representation is its character */
    override fun toString(): String {
        return character.toString()
    }
}
