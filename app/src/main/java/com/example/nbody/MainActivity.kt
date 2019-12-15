package com.example.nbody

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nbody.GravSim.BHTree
import com.example.nbody.GravSim.Body
import com.example.nbody.GravSim.Square
import com.example.nbody.GravSim.Vector2d
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var display: Display
    val sceneObjects = ArrayList<Body>()
    val bodies = 200
    lateinit var quadtree: BHTree

    var dpHeight = 0
    var dpWidth = 0

    val bounds = Square(-1080.0, -1080.0, 4320.0)

    val random = Random(124124)

    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        display = Display(this)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        display.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN)

        val displayMetrics: DisplayMetrics = this.getResources().getDisplayMetrics()

        //flip for landscape
        dpWidth = displayMetrics.widthPixels
        dpHeight = displayMetrics.heightPixels

        //generate some random bodies
        for (i in 0 until 40) {
            sceneObjects.add(generateCW())
        }

        for (i in 0 until 40) {
            sceneObjects.add(generateCCW())
        }

        //the sun
        sceneObjects.add(Body(1.989E+30, Vector2d(900.0, 500.0), Vector2d(0.0, 0.0)))

        display.sceneObjects = sceneObjects

        startSimTimer()

        setContentView(display)
    }

    override fun onPause() {
        super.onPause()
        display.pause()
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        display.resume()
        startSimTimer()
    }

    fun startSimTimer() {
        timer = fixedRateTimer("timer", false, 0, 34) {
            this@MainActivity.runOnUiThread {
                calcTree()
            }
        }
    }

    fun calcTree() {
        //first step recreate quad
        quadtree = BHTree(bounds)

        //recursively insert each body
        sceneObjects.forEach { quadtree.insert(it) }

        //calculate acceleration vector
        sceneObjects.forEach {
            it.acceleration = Vector2d(0.0, 0.0)
            quadtree.findForce(it)
        }

        //move objects to new location - we do this here because
        //if we were to move it during the loop calcing the accel v
        sceneObjects.forEach {
            it.Movement(bounds)
        }
    }

    private fun generateCW() : Body
    {

        //random between 800-830, 530-560, -0.55 -0.6, -0.79- 0.84

        //random.nextDouble(5.96219, 5.98219)

        return Body(
            random.nextDouble(5.96219, 5.98219) * Math.pow(10.0, 24.0),
            Vector2d(
                random.nextDouble(795.0, 835.0),
                random.nextDouble(524.0, 566.0)
            ),
            Vector2d(
                random.nextDouble(-0.6, -0.55),
                random.nextDouble(-0.84, -0.79)
            ))
    }

    private fun generateCCW() : Body
    {
        return Body(
            random.nextDouble(5.96219, 5.98219) * Math.pow(10.0, 24.0),
            Vector2d(
                random.nextDouble(988.0, 1033.0),
                random.nextDouble(524.0, 566.0)
            ),
            Vector2d(
                random.nextDouble(0.55, 0.65),
                random.nextDouble(-0.81, -0.73)
            ))
    }

    //Vector2d(1001.24, 546.0), Vector2d(0.59, -0.82))

    private fun generateBodyAtRandomLocation(): Body {
        return Body(
            5.97219E+24,
            Vector2d(
                random.nextDouble(800.0, 1000.0),
                random.nextDouble(400.0, 600.0)
            ),
            Vector2d(
                random.nextDouble(-0.82, 0.82),
                random.nextDouble(-0.82, 0.82)
            ))
    }
}
