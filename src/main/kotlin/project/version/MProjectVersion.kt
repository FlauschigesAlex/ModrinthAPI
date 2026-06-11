@file:Suppress("unused")
@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.project.version

import at.flauschigesalex.rinth.utils.serialize.SemanticVersionSerialized
import at.flauschigesalex.rinth.project.version.file.MProjectVersionFile
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
@ConsistentCopyVisibility
data class MProjectVersion internal constructor(
    val id: String,
    val name: String,
    @SerialName("version_number") val version: SemanticVersionSerialized,
    @SerialName("version_type") val channel: MProjectVersionType,
    val downloads: Int,
    val changelog: String,
    @SerialName("game_versions") val gameVersions: Set<String> = emptySet(),
    @SerialName("loaders") val loaders: Set<String> = emptySet(),
    @SerialName("date_published") private val _releaseDate: String,
    val files: ProjectVersionFiles,
): Comparable<MProjectVersion> {
    companion object;
    
    @Transient lateinit var slug: String
        internal set
    
    val downloadUrl: URL
        get() = URI.create("https://modrinth.com/plugin/${slug}/version/${id}").toURL()
    
    val releaseDate: Instant
        get() = ZonedDateTime.parse(_releaseDate).toInstant()

    override fun compareTo(other: MProjectVersion): Int = compareBy<MProjectVersion> { it.version }
        .thenBy { it.channel }
        .thenBy { it.releaseDate }
        .compare(this, other)
}

fun Iterable<MProjectVersion>.latestOrNull() = this.maxOrNull()
fun Iterable<MProjectVersion>.latest() = this.max()

fun Iterable<MProjectVersion>.initialOrNull() = this.minOrNull()
fun Iterable<MProjectVersion>.initial() = this.min()

fun Iterable<MProjectVersion>.stability(atLeast: MProjectVersionType) = this.filter { it.channel <= atLeast }
fun Iterable<MProjectVersion>.channel(channel: MProjectVersionType) = this.filter { it.channel == channel }

typealias ProjectVersionFiles = Collection<MProjectVersionFile>
fun ProjectVersionFiles.primary() = this.first { it.primary }
fun ProjectVersionFiles.primaryOrNull() = this.firstOrNull { it.primary }