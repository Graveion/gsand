package com.example.nbody.GravSim


class Body(var mass: Double, var location: Vector2d, var velocity: Vector2d) {

    var acceleration: Vector2d
    var state: State

    init {
        acceleration = Vector2d(0.0, 0.0)
        state = State.Empty
    }


    //movement for the leap method
    fun Movement(dt: Double) {
        velocity.x += dt * acceleration.x / mass
        velocity.y += dt * acceleration.y / mass
        location.x += dt * this.velocity.x
        location.y += dt * this.velocity.y
    }

    fun Movement(bounds: Square) {
        //we use the acceleration vector to update the velocity
        //possibly don't need as this is only used to update loc?
        velocity.x += acceleration.x
        velocity.y += acceleration.y
        location.x += this.velocity.x
        location.y += this.velocity.y

        if (!bounds.contains(location.x, location.y)) {
            //we're oob, we will get stack overflow when we try to find where to place
            //so just set the particle back
            location.x -= this.velocity.x
            location.y -= this.velocity.y
            velocity.x = 0.01
            velocity.y = 0.01

        }
    }
}

enum class State {
    Empty,
    Filled
}