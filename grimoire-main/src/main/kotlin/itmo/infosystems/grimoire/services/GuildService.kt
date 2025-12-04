package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.models.Guild
import itmo.infosystems.grimoire.repositories.GuildRepository
import org.springframework.stereotype.Service

@Service
class GuildService(private val guildRepository: GuildRepository) {
    fun getAvailableGuilds(wizardId: Long) = guildRepository.findAvailableGuilds(wizardId)

    fun getGuildsByLevel(level: Int): List<Guild> {
        return guildRepository.findByLevel(level)
    }
}