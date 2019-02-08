package twerszko.watcher.notifier.sound

import javazoom.jl.player.Player
import twerszko.watcher.Visit
import twerszko.watcher.notifier.Notifier
import java.io.File
import java.net.URL


class GilfoyleNotifier : Notifier {
    override fun notify(visits: List<Visit>) {
        soundFile().inputStream().use { Player(it).play() }
    }

    private fun soundFile(): File {
        val tempFile = File(System.getProperty("java.io.tmpdir"), TEMP_FILE_NAME)
        if (!tempFile.exists()) {
            URL(URL).openStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output, 2048)
                }
            }
        }
        return tempFile
    }

    companion object {
        const val TEMP_FILE_NAME = "_LXWntfr_"
        private const val FILE_NAME = "sound.mp3"
        const val URL = "https://bitcoinvolatility.io/wp-content/plugins/btc-alert/$FILE_NAME"
    }
}