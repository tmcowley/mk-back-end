package tmcowley.appserver.objects;

final data class Key(val character: Char) {

    // equals, hashCode, toString all defined

    /** string repesentation is its char */
    override fun toString(): String {
        return character.toString();
    }
}
