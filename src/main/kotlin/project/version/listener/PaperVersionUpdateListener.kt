package at.flauschigesalex.rinth.project.version.listener

import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.project.version.MProjectVersionType
import at.flauschigesalex.rinth.project.version.toMProjectVersionType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class PaperVersionUpdateListener(plugin: JavaPlugin, private val invocation: MUpdateConsumer) : Listener, MUpdateListener {
    
    private var isRegistered: Boolean = false
    
    init {
        if (isRegistered.not()) {
            plugin.server.pluginManager.registerEvents(this, plugin)
            invocation(plugin.server)
        }
        
        isRegistered = true
    }
    
    override val version = plugin.pluginMeta.version.let { SemanticVersion.parseOrNull(it) }
    override val channel = version?.type?.toMProjectVersionType() ?: MProjectVersionType.RELEASE
    
    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPermission("version_checker").not() && player.isOp.not()) return
        
        invocation(player)
    }
}