package tmcowley.appserver

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class SingletonTest {

	init{
		Singleton
	}

	@Test
	fun `user code generation`() {
		// given, when
		val userCode = Singleton.getRandomUserCode()

		// then
		assert(!userCode.isEmpty())
	}

	@Test
	fun `find longest phrase length`() {
		val longestPhrase = Singleton.phrases.maxByOrNull { phrase -> phrase.length } ?: ""
		println("\nLongest phrase length: ${longestPhrase.length}\n")
	}

	@Test
	fun `get longest word in word list`() {
		println("\nLongest word length: ${Singleton.maxWord?.length}\n")
	}
}
