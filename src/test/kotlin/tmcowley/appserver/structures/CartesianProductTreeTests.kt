package tmcowley.appserver.structures

import kotlin.math.pow

// junit5
import org.junit.jupiter.api.Test

// for fluent assertions
import org.assertj.core.api.Assertions.assertThat

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull

import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton
import tmcowley.appserver.models.Key

@SpringBootTest
internal class CartesianProductTreeTests {

    // test using word tree as derived class
    var tree = WordTree()

    fun resetTree() {
        this.tree = WordTree()
    }

    // allow exponentiation on integers
    infix fun Int.pow(exponent: Int): Int = toDouble().pow(exponent).toInt()

    @Test
    fun `test insertion and leaf collection`() {

        repeat(10) { index ->

            this.resetTree()

            // insert an arbitrary key index times
            repeat(index) {
                val keyPair = Singleton.getKeyPairOrNull(Key('q'))
                assertNotNull(keyPair)

                this.tree.insertKeyPair(keyPair)
            }

            // ensure tree leaf count is 2^index
            assertThat(this.tree.getLeaves().size).isEqualTo(2.pow(index))
        }

        this.resetTree()
    }
}
