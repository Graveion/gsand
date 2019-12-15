package com.example.nbody.GravSim


class Square(val x : Double, val y : Double, val length : Double) {
    var location : Vector2d
    var left : Double
    var right : Double
    var top : Double
    var bottom : Double
    var subdiv : Double

    init {
        location = Vector2d(x,y)
        left = x
        top = y
        right = x + length
        bottom = y + length
        subdiv = length/2
    }

    fun contains(oX : Double, oY : Double) : Boolean
    {
        return oX > x && oX < x+length && oY > y && oY < y+length
    }

    fun squareNW(): Square {
        return Square(x, y, subdiv)
    }

    fun squareNE(): Square {
        return Square(x+subdiv, y, subdiv)
    }

    fun squareSW(): Square {
        return Square(x, y+subdiv, subdiv)
    }

    fun squareSE(): Square {

        return Square(x+subdiv, y+subdiv, subdiv)
    }

}