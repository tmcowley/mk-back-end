package tmcowley.appserver.configurations

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/** configure CORS to allow local and hosting back-end origins */
@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedMethods("*")
            .allowedOrigins("http://localhost:3000", "https://localhost:3000", "https://www.tcowley.com", "https://tcowley.com", "https://mirrored-keyboard.vercel.app")
    }
}