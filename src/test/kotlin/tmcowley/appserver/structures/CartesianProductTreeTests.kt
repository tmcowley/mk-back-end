package tmcowley.appserver.structures

import tmcowley.appserver.utils.pow

// junit5
import org.junit.jupiter.api.Test

// for fluent assertions
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.context.SpringBootTest

/** as CartesianProductTree is abstract, we test using the derived class WordTree */
@SpringBootTest
internal class CartesianProductTreeTests {

    @Test
    fun `test empty word`() {
        // given
        // a word of length zero
        val emptyString = ""

        // when
        // we compute the matching words
        val matchingWords = WordTree().getAllWords(emptyString)

        // then
        // ensure there are no matches
        assertThat(matchingWords.size).isEqualTo(0)
    }

    @Test
    fun `test insertion and leaf collection`() {
        // for indexes 1 to 10:
        (1 .. 10).forEach { index ->

            // given
            // a word of length index with arbitrary character (without loss of generality)
            val wordOfLengthIndex = buildString {
                repeat(index) {
                    append('q')
                }
            }

            // when
            // we compute the matching words
            val matchingWords = WordTree().getAllWords(wordOfLengthIndex)

            // then
            // ensure tree leaf count is at most 2^index (matching words has distinct elements)
            assertThat(matchingWords.size).isEqualTo(2.pow(index))
        }
    }
}
