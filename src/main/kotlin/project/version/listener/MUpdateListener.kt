package at.flauschigesalex.rinth.project.version.listener

import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.rinth.project.version.MProjectVersionType

interface MUpdateListener {
    
    val version: SemanticVersion?
    val channel: MProjectVersionType
}