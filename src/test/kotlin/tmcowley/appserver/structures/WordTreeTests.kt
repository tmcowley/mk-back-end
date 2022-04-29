package tmcowley.appserver.structures

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat

@SpringBootTest
internal class WordTreeTests {

    @Test
    fun `test getting words and inserting key-pair`() {
        // given
        val words = listOf("test", "fire", "water", "earth", "air")

        words.forEach { word ->
            // when
            // we compute the matching words
            val matchingWords = WordTree().getWords(word)

            // then
            // ensure the word appears in the results
            assertThat(matchingWords).contains(word)
        }
    }
}
