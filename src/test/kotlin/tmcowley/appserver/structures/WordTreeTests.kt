package tmcowley.appserver.structures

import kotlin.math.pow
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key

@SpringBootTest
class WordTreeTests {

    var tree = WordTree()

    fun resetTree() {
        this.tree = WordTree()
    }

    @Test
    fun `test getting words and inserting key-pair`() {

        val words = listOf( "test", "fire", "water", "earth", "air" )

        words.forEach { word ->
            this.resetTree()

            // add the word to the permutation tree
            word.forEach { char ->  
                this.tree.insertKeyPair(Singleton.getKeyPair(Key(char))!!)
            }

            // ensure the word appears in the results
            assert(this.tree.getWords().contains(word))
        }

        this.resetTree()
    }
}
