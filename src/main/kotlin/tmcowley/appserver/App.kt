package tmcowley.appserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.Singleton

@SpringBootApplication
class App {}

fun main(args: Array<String>) {
    runApplication<App>(*args)

    // output algorithm configuration
    run {
        val syntaxAnalysisEnabled: Boolean = (Singleton.prop.get("analyseSyntax") as String).toBoolean()
        val frequencyAnalysisEnabled: Boolean =
                (Singleton.prop.get("analyseFrequency") as String).toBoolean()

        println()

        if (syntaxAnalysisEnabled) println("Notice: Syntax analysis enabled")
        else println("Notice: Syntax analysis disabled")

        if (frequencyAnalysisEnabled) println("Notice: Frequency analysis enabled")
        else println("Notice: Frequency analysis disabled")
    }

    // args.forEach { arg -> println(arg)}
}
