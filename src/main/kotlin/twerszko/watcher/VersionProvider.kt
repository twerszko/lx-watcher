package twerszko.watcher

import picocli.CommandLine

internal class VersionProvider : CommandLine.IVersionProvider {
    override fun getVersion(): Array<String> {
        return arrayOf(this.javaClass.`package`.implementationVersion ?: "UNKNOWN")
    }
}