package tmcowley.appserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class App {

    init{
        // output algorithm configuration
        println("\nNotice: Syntax analysis ${
            if (Singleton.syntaxAnalysisEnabled) "enabled" else "disabled"
        }")

        println("Notice: Frequency analysis ${
            if (Singleton.frequencyAnalysisEnabled) "enabled" else "disabled"
        }\n")
    }
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
