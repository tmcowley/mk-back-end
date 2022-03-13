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

	@Test
	fun `find longest phrase length`() {
		val longestPhrase = Singleton.phraseList.maxByOrNull { phrase -> phrase.length } ?: ""
		println("\nLongest phrase length: ${longestPhrase.length}\n")
	}
}
