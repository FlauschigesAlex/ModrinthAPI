package at.flauschigesalex.rinth.version.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.audience.Audience

@Suppress("unused")
class VelocityVersionUpdateListener(server: ProxyServer, plugin: Any, private val invocation: (Audience) -> Unit) {
    
    private var isRegistered: Boolean = false
    
    init {
        if (isRegistered.not()) {
            server.eventManager.register(plugin, this)
            invocation(server)
        }
        
        isRegistered = true
    }
    
    @Subscribe
    private fun onJoin(event: PostLoginEvent) {
        val player = event.player
        if (player.hasPermission("version_checker").not()) return
        
        invocation(event.player)
    }
}