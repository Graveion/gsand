package com.example.nbody.GravSim


val G = 6.6726 * Math.pow(10.0, -11.0)

const val theta = 0.85

fun pointDistance(A: Vector2d, B: Vector2d): Double {
    val xsquared: Double = (B.x - A.x) * (B.x - A.x)
    val ysquared: Double = (B.y - A.y) * (B.y - A.y)
    return Math.sqrt(xsquared + ysquared)
}

fun minus(a: Vector2d, b: Vector2d): Vector2d {
    return Vector2d(b.x - a.x, b.y - a.y)
}

fun scale(a: Vector2d, Scale: Double): Vector2d {
    return Vector2d(a.x * Scale, a.y * Scale)
}

fun add(a: Vector2d, b: Vector2d): Vector2d {
    return Vector2d(a.x + b.x, a.y + b.y)
}

fun magnitude(a: Vector2d): Double {
    return Math.sqrt(magnitudeSquared(a))
}

fun magnitudeSquared(a: Vector2d): Double {
    return (a.x * a.x) + (a.y * a.y)
}

fun CalcDistance(A: Body, B: Body) {
    var distance: Double = pointDistance(A.location, B.location)
    //we check for distance greater than 0 here because newton's formula tends to infinity at zero
    //so as to reduce the number of objects that fly off due to huge increased acceleration
    if (distance > 5) {
        //scale out co-ordinate distance to millions of km e.g 100,100 -> 200,100 = 100million distance on the x axis
        distance *= 1000000000
        val distancesquared = distance * distance
        val force: Double = G * ((A.mass * B.mass) / distancesquared)
        val acceleration: Double = force / A.mass

        //vector between two objects, note that this calculates the vector distance
        //to travel there instantly ...
        var tempDistanceVector = minus(A.location, B.location)

        //... so scale the vector for the acceleration we feel in that direction
        val vectorMagnitude: Double = magnitude(tempDistanceVector)
        val vectorScale =
            Vector2d(tempDistanceVector.x / vectorMagnitude, tempDistanceVector.y / vectorMagnitude)
        tempDistanceVector = scale(vectorScale, acceleration)

        //and add it to the current vector we have
        A.acceleration = add(tempDistanceVector, A.acceleration)
    }
}