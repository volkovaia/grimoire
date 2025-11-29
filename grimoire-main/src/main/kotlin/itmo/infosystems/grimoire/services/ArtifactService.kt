package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.repositories.ArtifactRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class ArtifactService(private val artifactRepository: ArtifactRepository) {
    fun getArtifacts(wizardId: Long, pageable: Pageable) =
        artifactRepository.findArtifactsByWizardId(wizardId, pageable)
}