package DI.API.CrashHandler

import android.content.Context
import android.util.Log


class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log or store the crash
        Log.e("CrashHandler", "Fatal error: ${throwable.message}", throwable)

        // Important: Let the default handler handle the crash (to show system dialog or kill app)
        defaultHandler?.uncaughtException(thread, throwable)
    }
}
