package pro.shushi.pamirs.core.common.geometry;

import java.io.Serializable;
import java.util.Objects;

/**
 * 点
 *
 * @author Adamancy Zhang at 20:25 on 2025-02-07
 */
public class Point implements Serializable {

    private static final long serialVersionUID = -6188257658671168214L;

    private int x;

    private int y;

    public Point() {
        this(0, 0);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        this(point.getX(), point.getY());
    }

    public Point(java.awt.Point point) {
        this(point.x, point.y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + "]";
    }
}
