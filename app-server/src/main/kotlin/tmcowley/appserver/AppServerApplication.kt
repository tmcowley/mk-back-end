package tmcowley.appserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication;

@SpringBootApplication
class AppServerApplication

fun main(args: Array<String>) {
	runApplication<AppServerApplication>(*args)

	println("Changed jar")

	for (arg in args){
		println(arg)
	}
}
