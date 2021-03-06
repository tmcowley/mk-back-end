package tmcowley.appserver

import org.junit.jupiter.api.Test

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull

internal class SingletonControllersTests {

    @Test
    fun `get db controller instance`() {
        assertNotNull(SingletonControllers.db)
    }
}