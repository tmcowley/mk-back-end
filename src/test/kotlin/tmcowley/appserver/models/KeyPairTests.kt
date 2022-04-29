package tmcowley.appserver.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class KeyPairTests {

    @Test
    fun `to string`() {
        val keyPair = KeyPair('q', 'p')
        assertThat(keyPair.toString()).isEqualTo("(q, p)")
    }

    @Test
    fun `iterating key-pair`() {
        KeyPair('q', 'p').forEach { _ -> }
    }
}