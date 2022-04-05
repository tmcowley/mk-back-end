package tmcowley.appserver.structures

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tmcowley.appserver.models.Key

internal class StructureUtilsKtTest {

    // given
    private val keysToKeyPairs = getKeyPairHashMap()

    /** ensure a -> (a, b) for all mappings */
    @Test
    fun `ensure key mappings include original key`() {
        // then
        keysToKeyPairs.forEach { (key, keyPair) ->
            assertThat(keyPair.contains(key))
        }
    }

    @Test
    fun `ensure full alphabet mapped`() {
        // then
        ('a' .. 'z').forEach { char ->
            val key = Key(char)
            assertThat(keysToKeyPairs.containsKey(key))
        }
    }
}