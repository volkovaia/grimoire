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
    fun joinGuild(wizardId: Long, newGuildId: Long): Wizard {
        val wizard = wizardRepository.findById(wizardId)
                .orElseThrow { IllegalStateException("Wizard not found") }
        val newGuild = guildRepository.findById(newGuildId)
                .orElseThrow { IllegalStateException("New Guild not found") }

        val currentLevel = wizard.guild?.level ?: 1 // Получаем текущий уровень мага (по умолчанию 1)

        if (newGuild.level != currentLevel + 1) {
            throw IllegalStateException("Guild not available for this wizard: expected level ${currentLevel + 1}, but got ${newGuild.level}")
        }

        wizard.guild = newGuild
        return wizardRepository.save(wizard)
    }


}