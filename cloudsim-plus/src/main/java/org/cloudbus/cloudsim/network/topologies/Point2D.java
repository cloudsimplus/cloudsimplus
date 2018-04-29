package org.cloudbus.cloudsim.network.topologies;

/**
 * A class to represent the coordinates of a 2-dimensional point.
 */
public class Point2D {
    private int x;
    private int y;

    /**
     * Creates a origin point with coordinates 0,0.
     */
    public Point2D(){
        this(0,0);
    }

    public Point2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("x: %d y: %d", x, y);
    }
}
