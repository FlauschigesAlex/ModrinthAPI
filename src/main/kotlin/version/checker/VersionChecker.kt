@file:Suppress("unused")

package at.flauschigesalex.rinth.version.checker

import at.flauschigesalex.rinth.version.ProjectVersionType

object VersionChecker {
    fun check(slug: String, channel: ProjectVersionType = ProjectVersionType.RELEASE) = VersionCheck(slug, channel)
}