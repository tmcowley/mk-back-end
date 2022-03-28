package tmcowley.appserver.structures

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NodeTest {

    private val parent = Node<String>("parent")
    private val middle = Node<String>("middle")
    private val child = Node<String>("child")

    init {
        // set middle node's parent and add child
        middle.setParent(parent)
        middle.addChild(child)
    }

    @Test
    fun `destructuring with componentN functions`() {
        val (value, parent, children) = middle

        assertThat(value).isEqualTo("middle")
        assertThat(parent).isEqualTo(parent)
        assertThat(children).isEqualTo(mutableListOf(child))
    }
}
