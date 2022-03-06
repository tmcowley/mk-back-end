package tmcowley.appserver

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SingletonTest {

	init{
		Singleton
	}

	@Test
	fun `user code generation`() {
		val userCode = Singleton.getRandomUserCode()
		// println(userCode)

		assert(!userCode.isEmpty())
	}
}
