package at.flauschigesalex.rinth.version.checker

import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.version.checker.parse.MinecraftVersion
import at.flauschigesalex.rinth.version.checker.parse.ModrinthLoader
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import kotlin.jvm.optionals.getOrNull

/**
 * Parses the plugin version using the provided parser function.
 * Supports [JavaPlugin], [ProxyServer] any [PluginContainer] by default.
 * @param clazz The class to parse the version for.
 * @param parser The function to parse the version.
 */
data class VersionParser<T: Any>(val clazz: Class<T>, private val parser: T.(slug: String) -> SemanticVersion?) {
    
    companion object : Iterable<VersionParser<*>> {
        
        private val _entries = mutableSetOf<VersionParser<*>>()
        val entries: Set<VersionParser<*>> get() = _entries

        override fun iterator(): Iterator<VersionParser<*>> = _entries.iterator()
        
        private fun add(parser: VersionParser<*>): Boolean {
            return _entries.add(parser)
        }
        
        private fun remove(parser: VersionParser<*>): Boolean {
            return _entries.remove(parser)
        }
        private fun remove(clazz: Class<*>): Boolean {
            return _entries.removeIf { it.clazz == clazz }
        }
        
        init {
            runCatching { // PAPERMC
                this.add(VersionParser(Server::class.java) { slug ->
                    this.pluginManager.getPlugin(slug)?.pluginMeta?.version?.let {
                        SemanticVersion.parseOrThrow(it)
                    }
                }.apply { 
                    this.loaders = setOf(ModrinthLoader.of(Bukkit.getServer().name.lowercase()))
                })
                this.add(VersionParser(JavaPlugin::class.java) {
                    SemanticVersion.parseOrThrow(this.pluginMeta.version)
                }.apply {
                    this.loaders = setOf(ModrinthLoader.of(Bukkit.getServer().name.lowercase()))
                })
            }
            
            runCatching { // VELOCITY
                this.add(VersionParser(ProxyServer::class.java) { slug ->
                    this.pluginManager.getPlugin(slug).getOrNull()?.description?.version?.getOrNull()?.let {
                        return@let SemanticVersion.parseOrThrow(it)
                    }
                }.apply {
                    this.loaders = setOf(ModrinthLoader.of("velocity"))
                })
                this.add(VersionParser(PluginContainer::class.java) {
                    this.description.version.getOrNull()?.let {
                        return@let SemanticVersion.parseOrThrow(it)
                    }
                }.apply {
                    this.loaders = setOf(ModrinthLoader.of("velocity"))
                })
            }
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    operator fun invoke(any: Any, slug: String): SemanticVersion? {
        return this.parser(any as T, slug)
    }
    
    var loaders: Set<ModrinthLoader> = emptySet()
    var versions: Set<MinecraftVersion> = emptySet()
    
    override fun equals(other: Any?): Boolean = other is VersionParser<*> && other.clazz == this.clazz
    override fun hashCode(): Int = this.clazz.hashCode()
}
