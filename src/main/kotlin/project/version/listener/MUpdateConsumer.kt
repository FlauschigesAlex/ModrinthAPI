package at.flauschigesalex.rinth.project.version.listener

import net.kyori.adventure.audience.Audience

typealias MUpdateConsumer = MUpdateListener.(Audience) -> Unit
