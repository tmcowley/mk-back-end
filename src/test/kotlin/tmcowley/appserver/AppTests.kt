package tmcowley.appserver

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class AppTests {

    /** Test class added ONLY to cover main() invocation not covered by application tests*/
    @Test
    fun `entry point invocation`() {
        main(arrayOf("testing"))
    }
}
