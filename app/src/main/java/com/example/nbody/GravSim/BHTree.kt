package com.example.nbody.GravSim

import androidx.appcompat.widget.VectorEnabledTintResources


class BHTree(val square: Square) {

    private var body: Body // body or aggregate body stored in this node
    private var NW // tree representing northwest quadrant
            : BHTree? = null
    private var NE // tree representing northeast quadrant
            : BHTree? = null
    private var SW // tree representing southwest quadrant
            : BHTree? = null
    private var SE // tree representing southeast quadrant
            : BHTree? = null

    var aggregateMass = 0.0
    var aggregateLocation = Vector2d(0.0, 0.0)

    init {
        body = Body(0.0, Vector2d(0.0, 0.0), Vector2d(0.0, 0.0))
    }

    //To insert a body b into the tree rooted at node x, use the following recursive procedure:
    //If node x is an internal node, update the center-of-mass and total mass of x. Recursively insert the body b in the appropriate quadrant.
    //If node x is an external node, say containing a body named c, then there are two bodies b and c in the same region.
    //Subdivide the region further by creating four children. Then, recursively insert both b and c into the appropriate quadrant(s).
    //Since b and c may still end up in the same quadrant, there may be several subdivisions during a single insertion.
    //Finally, update the center-of-mass and total mass of x.
    fun insert(B: Body) {
        if (CheckInternal())
        {
            checkQuadrant(B)

            if (CheckEmptyChildren()) { //children are empty we need to calculate CoM with aggregate data.
                NodeMassCentreAggregate()
            } else {
                NodeMassCentre()
            }
        }
        else  if (body.state == State.Empty) {
            //external
            body = B
            body.state = State.Filled
        }
        else
        {
            //I am an external node with an item and need to create subdivisions to fit in a new item and my own.
            //Since we have created new trees I should never be counted as external and thus no new nodes can
            //be attributed to me
            checkQuadrant(B)

            insert(body)

            body = Body(0.0, Vector2d(0.0,0.0), Vector2d(0.0, 0.0))
            body.state = State.Empty
        }
    }

    fun checkQuadrant(B: Body) {
        if (square.squareNW().contains(B.location.x, B.location.y)) {
            if (NW == null) {
                NW = BHTree(square.squareNW())
            }
            NW!!.insert(B)
        } else if (square.squareNE().contains(B.location.x, B.location.y)) {
            if (NE == null) {
                NE = BHTree(square.squareNE())
            }
            NE!!.insert(B)
        } else if (square.squareSW().contains(B.location.x, B.location.y)) {
            if (SW == null) {
                SW = BHTree(square.squareSW())
            }
            SW!!.insert(B)
        } else if (square.squareSE().contains(B.location.x, B.location.y)) {
            if (SE == null) {
                SE = BHTree(square.squareSE())
            }
            SE!!.insert(B)
        }
    }

    fun findForce(a: Body) {
        //Calculating the force acting on a body. To calculate the net force acting on body b, use the following recursive procedure,
        //starting with the root of the quad-tree:
        //If the current node is an external node then if it isn't b, calculate the force exerted by the current node on b,
        //Otherwise, calculate the ratio s / d.
        //Where s is the width of the region represented by the internal node, and d is the distance between the body and the node's center-of-mass.
        //If s / d < θ, treat this internal node as a single body
        //Otherwise, run the procedure recursively on each of the current node's children.

        if (!CheckInternal()) {

            if (a != body) {
                //external node so calc the acceleration vector for A based on this nodes mass
                //possible to be an external node without an atomic as all 4 quadrants are created at once
                if (body.state == State.Filled) {
                    CalcDistance(a, body)
                }
            }
            return
        } else {
            //internal node check for s/d
            val d = pointDistance(a.location, aggregateLocation)

            if ((square.length / d) < theta) {
                //s/d < theta so we aggregate this nodes data and calculate the acceleration vector..
                var nodeCentre = Body(aggregateMass, aggregateLocation, Vector2d(0.0, 0.0))
                CalcDistance(a, nodeCentre)
            } else {
                //recursively call for children
                NW?.findForce(a)
                NE?.findForce(a)
                SW?.findForce(a)
                SE?.findForce(a)
            }

        }

    }

    fun CentreOfMass(particles: ArrayList<Body>) : Body {
        //sum up masses
        val m = particles.map { it.mass }.sum()

        val x = particles.map { it.location.x * it.mass }.sum() / m
        val y = particles.map { it.location.y * it.mass }.sum() / m

        return Body(m, Vector2d(x, y), Vector2d(0.0, 0.0))
    }

    fun CentreOfMass(a: Body?, b: Body?): Body? {

        //m = m1 + m2
        //x = (x1m1 + x2m2) / m
        //y = (y1m1 + y2m2) / m
        //lazy null check for nodes

        //presumably for n particles it is
        //x = x1m1 + x2m2 + ... xNmN

        if (b == null) {
            return a
        }
        if (a == null) {
            return b
        }
        if (a.location == null && b.location == null) {
            return null
        }
        val m: Double = a.mass + b.mass
        val x: Double = (a.location.x * a.mass + b.location.x * b.mass) / m
        val y: Double = (a.location.y * a.mass + b.location.y * b.mass) / m
        return Body(m, Vector2d(x, y), Vector2d(0.0, 0.0))
    }

    fun NodeMassCentre() {

        val activeNodes = ArrayList<Body>()

        if (NW != null) activeNodes.add(NW!!.body)
        if (NE != null) activeNodes.add(NE!!.body)
        if (SW != null) activeNodes.add(SW!!.body)
        if (SE != null) activeNodes.add(SE!!.body)

        var centre = CentreOfMass(activeNodes)

        aggregateLocation = centre.location
        aggregateMass = centre.mass
    }

    fun NodeMassCentreAggregate() {
        val activeNodes = ArrayList<Body>()

        //needs to be the aggregate data of the nodes.
        if (NW?.aggregateMass != 0.0 && NW?.aggregateLocation != null) {
            activeNodes.add(Body(NW!!.aggregateMass, NW!!.aggregateLocation, Vector2d(0.0, 0.0)))
        }

        if (NE?.aggregateMass != 0.0 && NE?.aggregateLocation != null) {
            activeNodes.add(Body(NE!!.aggregateMass, NE!!.aggregateLocation, Vector2d(0.0, 0.0)))
        }
        if (SE?.aggregateMass != 0.0 && SE?.aggregateLocation != null) {
            activeNodes.add(Body(SE!!.aggregateMass, SE!!.aggregateLocation, Vector2d(0.0, 0.0)))
        }
        if (SW?.aggregateMass != 0.0 && SW?.aggregateLocation != null) {
            activeNodes.add(Body(SW!!.aggregateMass, SW!!.aggregateLocation, Vector2d(0.0, 0.0)))
        }

        val centre = CentreOfMass(activeNodes)
        aggregateLocation = centre.location
        aggregateMass = centre.mass
    }


    fun CheckInternal(): Boolean {
        //if all my children are empty then I am external
        return !(NW == null && NE == null && SW == null && SE == null)
    }

    fun CheckEmptyChildren(): Boolean {
        return (NW?.body == null && SW?.body == null && NE?.body == null && SE?.body == null)
    }

}