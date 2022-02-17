package tmcowley.appserver.objects;

data class Key(val character: Char) {

    // equals, hashCode, toString all defined

    override fun toString(): String {
        return character.toString();
    }
}