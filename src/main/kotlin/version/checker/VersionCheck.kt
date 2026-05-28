@file:Suppress("unused")

package at.flauschigesalex.rinth.version.checker

import at.flauschigesalex.lib.base.general.Cache
import at.flauschigesalex.lib.base.general.CacheEntry
import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.ModrinthAPI
import at.flauschigesalex.rinth.version.*
import at.flauschigesalex.rinth.version.checker.parse.MinecraftVersion
import at.flauschigesalex.rinth.version.checker.parse.ModrinthLoader
import java.time.Duration
import java.time.Instant

@ConsistentCopyVisibility
data class VersionCheck internal constructor(
    private val slug: String,
    private val channel: ProjectVersionType
) {
    
    private val cacheSlug = "modrinth-version-list@${slug}#${channel}"

    suspend fun versions(loader: Set<ModrinthLoader>? = null, minecraftVersion: Set<MinecraftVersion>? = null): Result<Set<ProjectVersion>> = (Cache[cacheSlug] ?: runCatching {
        val versions = ModrinthAPI.findAll(slug, loader?.map { it.name }?.toSet(),  minecraftVersion?.map { it.raw }?.toSet()).getOrThrow().toSet()
        return@runCatching versions
    }.also { Cache.put<Result<Set<ProjectVersion>>>(cacheSlug, CacheEntry(it).apply { 
        this.expiration = Instant.now() + Duration.ofHours(6)
    }) }).map { it.stability(channel).toSet() }
    
    suspend fun latestVersion(loader: Set<ModrinthLoader>? = null, minecraftVersion: Set<MinecraftVersion>? = null): Result<ProjectVersion> =
        versions(loader, minecraftVersion).map { it.latest() }
    
    suspend fun versionOf(version: SemanticVersion, loader: Set<ModrinthLoader>? = null, minecraftVersion: Set<MinecraftVersion>? = null): Result<ProjectVersion?> = runCatching { 
        val versions = this.versions(loader, minecraftVersion)
        return@runCatching versions.getOrThrow().find { it.version == version }
    }
    suspend fun versionOf(version: String): Result<ProjectVersion?> =
        SemanticVersion.parse(version).map { this.versionOf(it).getOrThrow() }
    
    /**
     * @param source A class that is supported by [VersionParser].
     */
    suspend fun currentVersion(source: Any): Result<ProjectVersion?> = runCatching {
        
        val parser = VersionParser.find { it.clazz.isInstance(source) }
            ?: throw VersionParseException("Failed to find parser for class ${source::class.java.name}.")
        val version = parser.invoke(source, slug)
        
        return@runCatching versions(parser.loaders, parser.versions).getOrThrow().find { it.version == version }
    }

    /**
     * @param source A class that is supported by [VersionParser].
     */
    suspend fun currentVersionDiff(source: Any): Result<ProjectVersionDiff?> = runCatching { 
        val versions = this.versions().getOrThrow()
        val version = versions.latestOrNull() ?: return@runCatching null
        val current = this.currentVersion(source).getOrThrow()
            ?: throw VersionParseException("Current version could not be parsed.")

        if (version == current) return@runCatching null
        return@runCatching ProjectVersionDiff(version, current, versions)
    }
}

class VersionParseException(message: String) : Exception(message)