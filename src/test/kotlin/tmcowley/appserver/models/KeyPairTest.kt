package tmcowley.appserver.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class KeyPairTest {

    @Test
    fun `to string`() {
        val keyPair = KeyPair('q', 'p')
        assertThat(keyPair.toString()).isEqualTo("(q, p)")
    }
}