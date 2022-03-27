package tmcowley.appserver.structures

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull

import tmcowley.appserver.Singleton
import tmcowley.appserver.models.Key

@SpringBootTest
class WordTreeTests {

    var tree = WordTree()

    fun resetTree() {
        this.tree = WordTree()
    }

    @Test
    fun `test getting words and inserting key-pair`() {

        val words = listOf("test", "fire", "water", "earth", "air")

        words.forEach { word ->
            this.resetTree()

            // add the word to the permutation tree
            word.forEach { char ->
                val keyPair = Singleton.getKeyPairOrNull(Key(char))
                // assertThat(keyPair).isNotNull()
                // keyPair ?: return

                assertNotNull(keyPair)

                this.tree.insertKeyPair(keyPair)
            }

            // ensure the word appears in the results
            assertThat(this.tree.getWords()).contains(word)
        }

        this.resetTree()
    }
}
