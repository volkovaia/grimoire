package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Artifact
import itmo.infosystems.grimoire.models.WizardArtifact
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface ArtifactRepository : JpaRepository<WizardArtifact, Long> {
    @Query("SELECT wa.artifact FROM WizardArtifact wa WHERE wa.wizard.id = :wizardId")
    fun findArtifactsByWizardId(
            @Param("wizardId") wizardId: Long,
            pageable: Pageable
    ): Page<Artifact>


}