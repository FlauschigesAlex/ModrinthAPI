@file:Suppress("unused")

package at.flauschigesalex.rinth.utils.checker.version

import at.flauschigesalex.rinth.project.version.MProjectVersionType

object VersionChecker {
    fun check(slug: String, channel: MProjectVersionType = MProjectVersionType.RELEASE) = VersionCheck(slug, channel)
}