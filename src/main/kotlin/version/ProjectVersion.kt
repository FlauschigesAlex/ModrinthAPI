@file:Suppress("unused")
@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.version

import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.version.file.ProjectVersionFile
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.ZonedDateTime

@Serializable
@JsonIgnoreUnknownKeys
data class ProjectVersion(
    val id: String,
    val name: String,
    @SerialName("version_number") val version: SemanticVersion,
    @SerialName("version_type") val channel: ProjectVersionType,
    val downloads: Int,
    val changelog: String,
    @SerialName("game_versions") val gameVersions: Set<String> = emptySet(),
    @SerialName("loaders") val loaders: Set<String> = emptySet(),
    @SerialName("date_published") private val _releaseDate: String,
    val files: ProjectVersionFiles,
): Comparable<ProjectVersion> {
    companion object;
    
    @Transient lateinit var slug: String
        internal set
    
    val downloadUrl: URL
        get() = URI.create("https://modrinth.com/plugin/${slug}/version/${id}").toURL()
    
    val releaseDate: Instant
        get() = ZonedDateTime.parse(_releaseDate).toInstant()

    override fun compareTo(other: ProjectVersion): Int = compareBy<ProjectVersion> { it.version }
        .thenBy { it.releaseDate }
        .compare(this, other)
}

fun Iterable<ProjectVersion>.latestOrNull() = this.maxOrNull()
fun Iterable<ProjectVersion>.latest() = this.max()

fun Iterable<ProjectVersion>.initialOrNull() = this.minOrNull()
fun Iterable<ProjectVersion>.initial() = this.min()

fun Iterable<ProjectVersion>.stability(atLeast: ProjectVersionType) = this.filter { it.channel <= atLeast }
fun Iterable<ProjectVersion>.channel(channel: ProjectVersionType) = this.filter { it.channel <= channel }

typealias ProjectVersionFiles = Collection<ProjectVersionFile>
fun ProjectVersionFiles.primary() = this.first { it.primary }
fun ProjectVersionFiles.primaryOrNull() = this.firstOrNull { it.primary }