package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.services.GuildService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import itmo.infosystems.grimoire.models.Guild
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal


//@RestController
//@RequestMapping("/guilds")
//class GuildController(private val guildService: GuildService) {
//
//    @GetMapping("/available")
//    fun getAvailableGuilds(@AuthenticationPrincipal wizardId: String): List<Guild> {
//        return guildService.getAvailableGuilds(wizardId.toLong())
//    }
//
//}

@RestController
@RequestMapping("/guilds")
class GuildController(private val guildService: GuildService) {

    // 1. НОВЫЙ МЕТОД: Обрабатывает GET /guilds?level=X
    // Не требует @AuthenticationPrincipal, что соответствует публичному доступу для регистрации.
    @GetMapping
    fun getGuildsByLevel(@RequestParam level: Int): List<Guild> {
        // Проверьте, что этот метод существует в вашем GuildService
        return guildService.getGuildsByLevel(level)
    }

    // 2. Ваш существующий метод (требует аутентификации)
    @GetMapping("/available")
    fun getAvailableGuilds(@AuthenticationPrincipal wizardId: String): List<Guild> {
        return guildService.getAvailableGuilds(wizardId.toLong())
    }
}