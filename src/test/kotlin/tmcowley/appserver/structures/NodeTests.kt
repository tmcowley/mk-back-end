package tmcowley.appserver.structures

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NodeTests {

    // given
    private val parent = Node("parent")
    private val middle = Node("middle")
    private val child = Node("child")

    init {
        // set middle node's parent and add child
        middle.setParent(parent)
        middle.addChild(child)
    }

    @Test
    fun `destructuring with componentN functions`() {
        // when
        val (value, parent, children) = middle

        // then
        assertThat(value).isEqualTo("middle")
        assertThat(parent).isEqualTo(parent)
        assertThat(children).isEqualTo(mutableListOf(child))
    }
}
