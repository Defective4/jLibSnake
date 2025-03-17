package io.github.defective4.jlibsnake.sprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.defective4.jlibsnake.LibSnake;

public class Snake {
    public static final byte DOWN = 12;

    public static final byte LEFT = 13;
    public static final byte RIGHT = 14;
    public static final byte UP = 11;
    private byte direction = RIGHT;
    private boolean directionChanged;

    private final LibSnake game;

    private byte movedDirection = RIGHT;

    private final List<Segment> segments = new ArrayList<>();
    private int x, y;

    public Snake(int x, int y, LibSnake game) {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    public Snake(LibSnake game) {
        this.game = game;
    }

    public void appendSegment() {
        byte dir;
        int originX, originY;
        if (segments.isEmpty()) {
            dir = direction;
            originX = x;
            originY = y;
        } else {
            Segment seg = segments.get(segments.size() - 1);
            dir = game.getStoredDirection(seg.getX(), seg.getY());
            originX = seg.getX();
            originY = seg.getY();
        }
        int x = 0;
        int y = 0;
        switch (dir) {
            default -> {
                x = 0;
                y = 0;
            }
            case DOWN -> y = -1;
            case UP -> y = 1;
            case LEFT -> x = 1;
            case RIGHT -> x = -1;
        }
        x += originX;
        y += originY;
        x %= game.getWidth();
        y %= game.getHeight();
        if (x < 0) x = game.getWidth() - 1;
        if (y < 0) y = game.getHeight() - 1;
        segments.add(new Segment(x, y));
        game.storeDirection(x, y, dir);
    }

    public void appendSegments(int count) {
        for (int i = 0; i < count; i++) appendSegment();
    }

    public byte getDirection() {
        return direction;
    }

    public byte getMovedDirection() {
        return movedDirection;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveAllSegments() {
        for (Segment pt : segments) {
            int x = pt.getX();
            int y = pt.getY();
            byte dir = game.getStoredDirection(x, y);
            switch (dir) {
                default -> {}
                case LEFT -> x--;
                case RIGHT -> x++;
                case DOWN -> y++;
                case UP -> y--;
            }
            x %= game.getWidth();
            y %= game.getHeight();
            if (x < 0) x = game.getWidth() - 1;
            if (y < 0) y = game.getHeight() - 1;
            pt.setX(x);
            pt.setY(y);
            pt.setLastDirection(dir);
        }
    }

    public void setDirection(byte direction) {
        if (direction != UP && direction != DOWN && direction != LEFT && direction != RIGHT)
            throw new IllegalArgumentException("Invalid direction");
        if (directionChanged || isOpposite(this.direction, direction)) return;
        directionChanged = true;
        this.direction = direction;
    }

    public void setDirectionChanged(boolean directionChanged) {
        this.directionChanged = directionChanged;
    }

    public void setMovedDirection(byte movedDirection) {
        this.movedDirection = movedDirection;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static boolean isOpposite(int dir1, int dir2) {
        return dir1 == LEFT && dir2 == RIGHT || dir1 == RIGHT && dir2 == LEFT || dir1 == UP && dir2 == DOWN
                || dir1 == DOWN && dir2 == UP;
    }

}
