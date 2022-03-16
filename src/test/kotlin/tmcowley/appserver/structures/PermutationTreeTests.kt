package tmcowley.appserver.structures

import kotlin.math.pow
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key

@SpringBootTest
class PermutationTreeTests {

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

            repeat(index) { this.tree.insertKeyPair(Singleton.getKeyPair(Key('q'))!!) }
            assert(this.tree.getLeaves().size == 2.pow(index))

            // debugging
            // if (index == 2) (this.tree.getLeaves().forEach{ leaf -> println(leaf.value.toString()) })
        }

        this.resetTree()
    }
}
