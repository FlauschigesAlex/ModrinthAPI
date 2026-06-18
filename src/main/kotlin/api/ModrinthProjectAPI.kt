package at.flauschigesalex.rinth.api

import at.flauschigesalex.lib.base.file.json.JsonManager
import at.flauschigesalex.lib.base.file.json.deserializeOrThrow
import at.flauschigesalex.lib.base.general.HttpRequestHandler
import at.flauschigesalex.rinth.project.MProject
import java.net.URLEncoder

@Suppress("unused")
object ModrinthProjectAPI {
    private const val URI = "https://api.modrinth.com/v2/project/%s"
    
    suspend fun findOne(slug: String): Map.Entry<String, Result<MProject>> = this.findAll(listOf(slug)).entries.first()
    suspend fun findAll(slugs: Collection<String>): Map<String, Result<MProject>> {
        val slugSet = slugs.toSet()

        val results = slugSet.map { slug ->
            slug to runCatching {
                val uri = URI.format(URLEncoder.encode(slug, "UTF-8"))

                val request = HttpRequestHandler(uri) ?: throw Error("Failed to create HTTP request from: '$uri'")
                val response = request.get(JsonManager.BodyHandler) ?: throw Error("Failed to get response from: '$uri'")
                
                val json = response.body() ?: throw Error("Failed to parse JSON response from: '$uri'")
                val project = json.deserializeOrThrow<MProject>()
                
                return@runCatching project
            }
        }.toMap()

        return results
    }
}