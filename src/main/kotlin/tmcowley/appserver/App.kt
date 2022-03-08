package tmcowley.appserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.Singleton

@SpringBootApplication
class App {}

fun main(args: Array<String>) {
    runApplication<App>(*args)

    // init singletons
    // Singleton
    SingletonControllers

    // val p: Key = Key('p');
    // val q: Key = Key('q');

    // val qp: KeyPair = KeyPair(q, p);

    // println(qp.toString());

    // var structs = DataStructures();

    // println(structs.getWordSet().toString());

    for (arg in args) {
        println(arg)
    }
}
