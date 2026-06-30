package at.flauschigesalex.rinth.project.version.listener

import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.project.version.MProjectVersionType
import at.flauschigesalex.rinth.project.version.toMProjectVersionType
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.audience.Audience
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

@Suppress("unused")
class VelocityVersionUpdateListener(server: ProxyServer, plugin: Any, private val invocation: MUpdateConsumer): MUpdateListener {
    companion object {
        @Deprecated("Legacy consumer", ReplaceWith("VelocityVersionUpdateListener(server, plugin, MUpdateConsumer)"))
        operator fun invoke(server: ProxyServer, plugin: Any, legacyInvocation: (Audience) -> Unit) =
            VelocityVersionUpdateListener(server, plugin) { legacyInvocation(it) }
    }
    
    private var isRegistered: Boolean = false
    
    init {
        if (isRegistered.not()) {
            server.eventManager.register(plugin, this)
            invocation(server)
        }
        
        isRegistered = true
    }

    override val version = run {
        val jarPath: Path? = plugin.javaClass.protectionDomain?.codeSource?.location?.toURI()?.let { Path.of(it) }
        val container = server.pluginManager.plugins.find { it.description.source.getOrNull() == jarPath }
        container?.description?.version?.getOrNull()?.let { SemanticVersion.parseOrNull(it) }
    }
    override val channel = version?.type?.toMProjectVersionType() ?: MProjectVersionType.RELEASE
    
    @Subscribe
    private fun onJoin(event: PostLoginEvent) {
        val player = event.player
        if (player.hasPermission("version_checker").not()) return
        
        invocation(event.player)
    }
}