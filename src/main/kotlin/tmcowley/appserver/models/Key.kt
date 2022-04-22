package tmcowley.appserver.models

/** 
 * a key is a character storing entity; 
 * Kotlin data class defines equals, hashCode, and toString
 */
data class Key(val character: Char) {

    /** string representation is its character */
    override fun toString(): String {
        return character.toString()
    }
}
