package tmcowley.appserver

import tmcowley.appserver.controllers.DatabaseController

object SingletonControllers {

    val db = DatabaseController()

    init {
        println("\nSingletonControllers init\n")
    }
}
