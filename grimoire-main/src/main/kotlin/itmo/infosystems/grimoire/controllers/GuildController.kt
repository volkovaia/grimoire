package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.requests.GuildJoinRequest
import itmo.infosystems.grimoire.services.GuildService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import itmo.infosystems.grimoire.models.Guild
import itmo.infosystems.grimoire.models.Wizard
import itmo.infosystems.grimoire.services.WizardService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/guilds")
class GuildController(private val guildService: GuildService, private val wizardService: WizardService) {


    @GetMapping
    fun getGuildsByLevel(@RequestParam level: Int): List<Guild> {
        return guildService.getGuildsByLevel(level)
    }

    @GetMapping("/available")
    fun getAvailableGuilds(@AuthenticationPrincipal wizardId: String): List<Guild> {
        return guildService.getAvailableGuilds(wizardId.toLong())
    }

    @PostMapping("/join")
    fun joinGuild(
            @AuthenticationPrincipal wizardId: String,
            @RequestBody request: GuildJoinRequest
    ): Wizard {
        return wizardService.joinGuild(wizardId.toLong(), request.guildId)
    }
}