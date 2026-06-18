package at.flauschigesalex.rinth.api

import at.flauschigesalex.lib.base.file.json.JsonManager
import at.flauschigesalex.lib.base.file.json.deserializeOrThrow
import at.flauschigesalex.lib.base.general.HttpRequestHandler
import at.flauschigesalex.rinth.project.version.MProjectVersion
import at.flauschigesalex.rinth.project.version.checker.VersionChecker as VC

@Suppress("unused")
object ModrinthVersionAPI {
    
    val VersionChecker = VC
    
    suspend fun findOne(slug: String, loader: String? = null, version: String? = null): Result<MProjectVersion?> =
        this.findAll(slug, loader, version).map { it.firstOrNull() ?: return Result.success(null) }

    suspend fun findOne(slug: String, loaders: Set<String>?, versions: Set<String>? = null): Result<MProjectVersion?> =
        this.findAll(slug, loaders, versions).map { it.firstOrNull() ?: return Result.success(null) }

    suspend fun findAll(slug: String, loader: String? = null, version: String? = null): Result<List<MProjectVersion>> =
        this.findAll(slug, loader?.let { setOf(it) }, version?.let { setOf(it) })

    suspend fun findAll(slug: String, loaders: Set<String>?, versions: Set<String>? = null): Result<List<MProjectVersion>> {
        val queries = mutableListOf<String>()

        loaders?.let { loaders -> queries.add("loaders=[${ loaders.joinToString { "%22${it}%22" } }]") }
        versions?.let { versions -> queries.add("game_versions=[${ versions.joinToString { "%22${it}%22" } }]") }

        var url = "https://api.modrinth.com/v2/project/${slug}/version"
        if (queries.isNotEmpty()) url += "?" + queries.joinToString("&")

        val request = HttpRequestHandler.Companion(url) ?: return Result.failure(Exception("Failed to create HTTP request."))
        val response = request.get(JsonManager.ListBodyHandler) ?: return Result.failure(Exception("Failed to get response from Modrinth."))
        val body = response.body() ?: return Result.failure(Exception("Failed to parse JSON response."))

        val versions = runCatching { body.map { it.deserializeOrThrow<MProjectVersion>() }.onEach { it.slug = slug } }
        return versions
    }
}