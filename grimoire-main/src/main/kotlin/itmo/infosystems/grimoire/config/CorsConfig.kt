package itmo.infosystems.grimoire.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        // **Конфигурация CORS для HTTP-запросов (POST, GET, PUT и т.д.)**
        registry.addMapping("/**") // Применить CORS ко всем HTTP-путям
                .allowedOriginPatterns("*") // Разрешить все источники (более безопасно указать конкретные порты: "http://localhost:8080", "http://127.0.0.1:5500" и т.п.)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
    }
}