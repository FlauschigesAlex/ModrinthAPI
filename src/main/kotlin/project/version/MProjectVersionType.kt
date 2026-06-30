package at.flauschigesalex.rinth.project.version

import at.flauschigesalex.lib.base.general.version.VersionType
import kotlinx.serialization.Serializable

@Serializable
@Suppress("unused")
enum class MProjectVersionType {
    RELEASE, BETA, ALPHA,;
    companion object {
        
        fun find(name: String) = entries.find { it.name.equals(name, true) }
    }
}

fun VersionType.toMProjectVersionType() = when (this) {
    VersionType.RELEASE -> MProjectVersionType.RELEASE
    VersionType.BETA, VersionType.RELEASE_CANDIDATE, VersionType.SNAPSHOT -> MProjectVersionType.BETA
    VersionType.ALPHA -> MProjectVersionType.ALPHA
}