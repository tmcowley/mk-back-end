package tmcowley.appserver.controllers

import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import tmcowley.appserver.Singleton

@CrossOrigin(origins = ["http://localhost:3000"], methods = [RequestMethod.GET])
@RestController
@RequestMapping(value = ["/get"])
class GetAPIs {

    init {}

    @GetMapping(value = ["/status"])
    fun status(): Boolean {
        // return true when active
        return true
    }

    @GetMapping(value = ["/random-phrase"])
    fun getRandomPhrase(): String {
        // get a random phrase from the phrase list
        return Singleton.getRandomPhrase()
    }
}
