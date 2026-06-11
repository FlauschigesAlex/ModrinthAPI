package at.flauschigesalex.rinth.project.version

import kotlinx.serialization.Serializable

@Serializable
enum class MProjectVersionType {
    RELEASE, BETA, ALPHA,;
    companion object {
        
        fun find(name: String) = entries.find { it.name.equals(name, true) }
    }
}