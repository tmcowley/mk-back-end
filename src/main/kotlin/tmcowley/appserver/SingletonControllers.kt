package tmcowley.appserver

import tmcowley.appserver.controllers.DatabaseController

object SingletonControllers {

    val db = DatabaseController()

    init {
        println("\n\nSingletonControllers init\n\n")
        db.createNewUser(21, 60)
    }
}