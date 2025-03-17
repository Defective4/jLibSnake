package io.github.defective4.jlibsnake.sprite;

import java.util.Objects;

public class Segment {
    private byte lastDirection = Snake.RIGHT;
    private int x, y;

    public Segment(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Segment other = (Segment) obj;
        return lastDirection == other.lastDirection && x == other.x && y == other.y;
    }

    public byte getLastDirection() {
        return lastDirection;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastDirection, x, y);
    }

    public void setLastDirection(byte lastDirection) {
        this.lastDirection = lastDirection;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Segment [lastDirection=" + lastDirection + ", x=" + x + ", y=" + y + "]";
    }

}
