@file:Suppress("unused")

package at.flauschigesalex.rinth.project.version.checker

import at.flauschigesalex.lib.base.general.Cache
import at.flauschigesalex.lib.base.general.CacheEntry
import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.api.ModrinthVersionAPI
import at.flauschigesalex.rinth.project.version.MProjectVersion
import at.flauschigesalex.rinth.project.version.MProjectVersionDifference
import at.flauschigesalex.rinth.project.version.MProjectVersionType
import at.flauschigesalex.rinth.project.version.latest
import at.flauschigesalex.rinth.project.version.latestOrNull
import at.flauschigesalex.rinth.project.version.stability
import at.flauschigesalex.rinth.project.version.checker.parse.MinecraftVersion
import at.flauschigesalex.rinth.project.version.checker.parse.ModrinthLoader
import at.flauschigesalex.rinth.utils.version.checker.VersionParser
import java.time.Duration
import java.time.Instant

@ConsistentCopyVisibility
data class VersionCheck internal constructor(
    private val slug: String,
    private val channel: MProjectVersionType
) {
    
    companion object {
        var CACHE_DURATION: Duration = Duration.ofHours(6)
    }
    
    private val cacheSlug = "modrinth-project.version-list@${slug}#${channel}"

    suspend fun versions(loader: Set<ModrinthLoader>? = null, minecraftVersion: Set<MinecraftVersion>? = null): Result<Set<MProjectVersion>> = (Cache[cacheSlug] ?: runCatching {
        val versions = ModrinthVersionAPI.findAll(slug, loader?.map { it.name }?.toSet(),  minecraftVersion?.map { it.raw }?.toSet()).getOrThrow().toSet()
        return@runCatching versions
    }.also { Cache.put<Result<Set<MProjectVersion>>>(cacheSlug, CacheEntry(it).apply { 
        this.expiration = Instant.now() + CACHE_DURATION
    }) }).map { it.stability(channel).toSet() }
    
    suspend fun latestVersion(loader: Set<ModrinthLoader>? = null, minecraftVersion: Set<MinecraftVersion>? = null): Result<MProjectVersion> =
        versions(loader, minecraftVersion).map { it.latest() }
    
    suspend fun versionOf(version: SemanticVersion, loader: Set<ModrinthLoader>? = null, minecraftVersion: Set<MinecraftVersion>? = null): Result<MProjectVersion?> = runCatching { 
        val versions = this.versions(loader, minecraftVersion)
        return@runCatching versions.getOrThrow().find { it.version == version }
    }
    suspend fun versionOf(version: String): Result<MProjectVersion?> =
        SemanticVersion.parse(version).map { this.versionOf(it).getOrThrow() }
    
    /**
     * @param source A class that is supported by [VersionParser].
     */
    suspend fun currentVersion(source: Any): Result<MProjectVersion> = runCatching {
        
        val parser = VersionParser.find { it.clazz.isInstance(source) }
            ?: throw VersionParseException("Failed to find parser for class ${source::class.java.name}.")
        val version = parser.invoke(source, slug)
            ?: throw VersionParseException("Version cannot be null for class ${source::class.java.name}.")
        
        val currentVersion = versions(parser.loaders, parser.versions).getOrThrow().find { it.version == version }
            ?: throw VersionParseException("Failed to find project.version ${version} on Modrinth.")
        
        return@runCatching currentVersion
    }

    /**
     * @param source A class that is supported by [VersionParser].
     */
    suspend fun currentVersionDiff(source: Any): Result<MProjectVersionDifference?> = runCatching { 
        val versions = this.versions().getOrThrow()
        val version = versions.latestOrNull() ?: return@runCatching null
        val current = this.currentVersion(source).getOrThrow()

        if (version == current) return@runCatching null
        return@runCatching MProjectVersionDifference(version, current, versions)
    }
}

class VersionParseException(message: String) : Exception(message)