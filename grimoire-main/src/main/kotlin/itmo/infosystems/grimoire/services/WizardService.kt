package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.models.Wizard
import itmo.infosystems.grimoire.repositories.GuildRepository
import itmo.infosystems.grimoire.repositories.WizardRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WizardService(
    private val wizardRepository: WizardRepository,
    private val guildRepository: GuildRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val wizard = wizardRepository.findByLogin(username)
            ?: throw UsernameNotFoundException("Wizard not found with login: $username")

        return org.springframework.security.core.userdetails.User(
            wizard.login,
            wizard.password,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
    }

    fun getWizard(id: Long): Wizard {
        return wizardRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Wizard with id $id not found") }
    }

    fun getWizardByLogin(login: String): Wizard {
        return wizardRepository.findByLogin(login)
            ?: throw EntityNotFoundException("Wizard not found with login $login")
    }

    @Transactional
    fun joinGuild(wizardId: Long, guildId: Long): Wizard {
        val wizard = wizardRepository.findById(wizardId)
            .orElseThrow { EntityNotFoundException("Wizard with id $wizardId not found") }

        val guild = guildRepository.findById(guildId)
            .orElseThrow { EntityNotFoundException("Guild with id $guildId not found") }

        val available = guildRepository.findAvailableGuilds(wizardId).any { it.id == guildId }
        if (!available) {
            throw IllegalStateException("Guild not available for this wizard")
        }
        wizard.guild = guild

        return wizardRepository.save(wizard)
    }
}