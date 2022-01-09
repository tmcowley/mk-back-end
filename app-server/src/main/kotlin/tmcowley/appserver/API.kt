package tmcowley.appserver

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
@RequestMapping(value=["/api"])
class API

@PostMapping("/")
fun getAll(): String{
    println("/api/ called")

    return "/api/ called"
}

@PostMapping(value=["/test"])
fun test(): String {
    println("/api/test called");

    return "/api/test called"
}

// @PostMapping(value=["/test"])
// fun test(@RequestBody body: kotlin.Any) {

//     println("/api/test called");

//     try {
//         println("test post mapping collected: $body")
//     } catch (e: Exception) {
//         println("printing request body failed");
//     }

//     return
// }
