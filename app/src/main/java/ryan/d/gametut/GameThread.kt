package ryan.d.gametut

import android.content.res.Resources
import android.graphics.Canvas
import android.view.SurfaceHolder
import java.lang.Exception

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView): Thread() {
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()
    private var running = false
    private var targetFPS = 60L
    private lateinit var gameViewModel: GameViewModel

    fun attachViewModel(model: GameViewModel) {
        gameViewModel = model
    }

    private var tick = 100
    override fun run() {
        var startTime = 0L
        val targetTime = 1000L / targetFPS

        while (running) {
            var canvas: Canvas? = null

            try {
                startTime = System.nanoTime()
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    updateGame()
                    gameView.draw(canvas)
                }
            } catch (e: Exception) {} finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            val timeMillis = (System.nanoTime() - startTime) / 1000000L
            val waitTime = targetTime - timeMillis

            --tick
            if(waitTime > 0L) {
                try {
                    sleep(waitTime)
                    if(tick <= 0) {
                        gameViewModel.newPurpleNote(gameView.resources)
                        tick = 100
                    }
                    if(tick == 50) {
                        gameViewModel.newBlueNote(gameView.resources)
                    }
                    if(tick == 70) {
                        gameViewModel.newGoldNote(gameView.resources)
                    }
                } catch (e: Exception) {}
            }
        }
    }

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    fun doClick(x: Int, y: Int): Boolean {
        return gameViewModel.doClick(x, y)
    }

    fun updateGame() {
        gameViewModel.checkNotes()
        gameViewModel.update()
    }
}