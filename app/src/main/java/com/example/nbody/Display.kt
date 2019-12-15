package com.example.nbody

import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.nbody.GravSim.Body
import com.example.nbody.GravSim.Vector2d
import kotlin.math.roundToInt

class Display(context: Context) : SurfaceView(context), Runnable {

    var running = false
    var canvasThread: Thread
    var surfaceHolder: SurfaceHolder
    val paint = Paint()
    var sceneObjects = ArrayList<Body>()

    override fun run() {
        var canvas: Canvas

        while (running) {
            if (surfaceHolder.getSurface().isValid()) {

                canvas = surfaceHolder.lockCanvas()
                canvas.save()
                canvas.drawColor(Color.BLACK)

                sceneObjects.forEach {
                    canvas.drawCircle(
                        it.location.x.toFloat(),
                        it.location.y.toFloat(),
                        3.14f,
                        paint
                    )

                    //debug txt
//                    canvas.drawText(
//                       "loc (x:" + it.location.x.roundToInt().toString() + ",y:" + it.location.y.roundToInt().toString() + ")"
//                        , it.location.x.toFloat(), it.location.y.toFloat(), paint)
                }



                canvas.restore()
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }

    }

    init {
        canvasThread = Thread(this)
        surfaceHolder = getHolder()
        paint.setColor(Color.YELLOW)
    }

    fun pause() {
        running = false

        try { // Stop the thread (rejoin the main thread)
            canvasThread.join()
        } catch (e: InterruptedException) {
        }
    }

    fun resume() {
        running = true
        canvasThread = Thread(this)
        canvasThread.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {

            }
            else -> {
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.setTextSize(24.0f)

    }


}