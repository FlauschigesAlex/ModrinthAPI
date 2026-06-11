package at.flauschigesalex.rinth.project.version.listener

import net.kyori.adventure.audience.Audience
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class PaperVersionUpdateListener(plugin: JavaPlugin, private val invocation: (Audience) -> Unit) : Listener {
    
    private var isRegistered: Boolean = false
    
    init {
        if (isRegistered.not()) {
            plugin.server.pluginManager.registerEvents(this, plugin)
            invocation(plugin.server)
        }
        
        isRegistered = true
    }
    
    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPermission("version_checker").not() && player.isOp.not()) return
        
        invocation(player)
    }
}