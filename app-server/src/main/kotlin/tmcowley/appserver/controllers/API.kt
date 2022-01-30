package tmcowley.appserver.controllers

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType.*;

// https://kotlinlang.org/docs/annotations.html#arrays-as-annotation-parameters
@RestController
@RequestMapping(
    value=["/api"],
    consumes=["application/json"]
)
class API {

    @PostMapping(value=["/convert/rhs"])
    fun convertToFull(input: String): Array<String> {
        return arrayOf("")
    }

    @PostMapping(value=["/convert/lhs"])
    fun convertToLHS(input: String): String {
        return ""
    }

    @PostMapping(value=["/test"])
    fun getAll(@RequestBody body: kotlin.Any): String {
        println("/api/test called");

        // var body: String? = null;

        try {
            println("test post mapping collected: $body")
        } catch (e: Exception) {
            println("printing request body failed");
        }

        return "/api/test called"
    }

}
