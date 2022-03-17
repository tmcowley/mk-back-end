package tmcowley.appserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import tmcowley.appserver.Singleton

@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)

    // output algorithm configuration
    run {
        println()

        if (Singleton.syntaxAnalysisEnabled) println("Notice: Syntax analysis enabled")
        else println("Notice: Syntax analysis disabled")

        if (Singleton.frequencyAnalysisEnabled) println("Notice: Frequency analysis enabled")
        else println("Notice: Frequency analysis disabled")
    }
}
