package devcavin.pesacore.config

import org.apache.catalina.filters.CorsFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfiguration {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:3000") // modify with a list of your client URLs
        config.allowedMethods = listOf("GET", "POST", "OPTIONS") // Only GET, POST and additionally OPTIONS are
        // supported on this project
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        source.registerCorsConfiguration("/**", config)

        return CorsFilter()
    }
}