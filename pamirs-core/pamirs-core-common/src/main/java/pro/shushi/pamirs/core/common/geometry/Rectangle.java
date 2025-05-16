package pro.shushi.pamirs.core.common.geometry;

import java.util.Objects;

/**
 * 矩形
 *
 * @author Adamancy Zhang at 21:56 on 2025-02-07
 */
public class Rectangle extends Point {

    private static final long serialVersionUID = 7789094194838001175L;

    private int width;

    private int height;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    public Rectangle(Rectangle rectangle) {
        this(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public Rectangle(java.awt.Rectangle rectangle) {
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public Rectangle(Point point) {
        this(point.getX(), point.getY(), 0, 0);
    }

    public Rectangle(Point point, int width, int height) {
        this(point.getX(), point.getY(), width, height);
    }

    public Rectangle(java.awt.Point point) {
        this(point.x, point.y, 0, 0);
    }

    public Rectangle(java.awt.Point point, int width, int height) {
        this(point.x, point.y, width, height);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void translate(int dx, int dy, int dw, int dh) {
        super.translate(dx, dy);
        this.width += dw;
        this.height += dh;
    }

    public boolean contains(int x, int y) {
        int sx = this.getSX(),
                sy = this.getSY(),
                ex = this.getEX(),
                ey = this.getEY();
        return sx <= x && x <= ex &&
                sy <= y && y <= ey;
    }

    public boolean contains(Point point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(java.awt.Point point) {
        return contains(point.x, point.y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSX() {
        return this.getX();
    }

    public int getEX() {
        return this.getX() + this.getWidth();
    }

    public int getSY() {
        return this.getY();
    }

    public int getEY() {
        return this.getY() + this.getHeight();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle) obj;
            return ((getX() == r.getX()) &&
                    (getY() == r.getY()) &&
                    (width == r.width) &&
                    (height == r.height));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), width, height);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[x=" + getX() + ",y=" + getY() + ",width=" + width + ",height=" + height + "]";
    }
}
