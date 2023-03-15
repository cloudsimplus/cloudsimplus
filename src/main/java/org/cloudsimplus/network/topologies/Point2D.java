package org.cloudsimplus.network.topologies;

/**
 * A class to represent the coordinates of a 2-dimensional point.
 * @param x horizontal coordinate
 * @param y vertical coordinate
 */
public record Point2D(int x, int y) {
    /**
     * Creates an origin point with coordinates 0,0.
     */
    public Point2D(){
        this(0,0);
    }

    @Override
    public String toString() {
        return "x: %d y: %d".formatted(x, y);
    }
}
