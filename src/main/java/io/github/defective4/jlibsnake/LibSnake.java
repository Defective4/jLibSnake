package io.github.defective4.jlibsnake;

import static io.github.defective4.jlibsnake.sprite.Snake.*;
import static io.github.defective4.jlibsnake.sprite.Sprites.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.defective4.jlibsnake.event.GameListener;
import io.github.defective4.jlibsnake.sprite.Point;
import io.github.defective4.jlibsnake.sprite.Segment;
import io.github.defective4.jlibsnake.sprite.Snake;

public class LibSnake {
    private final byte[][] directions;
    private final List<GameListener> listeners = new CopyOnWriteArrayList<>();
    private final List<Point> points = new ArrayList<>();
    private final Random rand = new Random();
    private final Snake snek;
    private byte[][] walls;
    private final int width, height;

    public LibSnake(int width, int height) {
        this.width = width;
        this.height = height;
        setMaze(initByteField(width, height));
        directions = initByteField(width, height);
        snek = new Snake(width / 2, height / 2, this);
        snek.appendSegments(2);
    }

    public boolean addListener(GameListener listener) {
        return listeners.add(listener);
    }

    public byte[][] getByteField() {
        byte[][] board = initByteField(width, height);
        byte sdir = snek.getDirection();
        int px = 0;
        int py = 0;
        switch (sdir) {
            default -> {}
            case RIGHT -> px = 1;
            case LEFT -> px = -1;
            case DOWN -> py = 1;
            case UP -> py = -1;
        }
        boolean has = hasPointAt(px + snek.getX(), py + snek.getY());
        board[snek.getX()][snek.getY()] = switch (snek.getMovedDirection()) {
            default -> SNAKE_RIGHT;
            case RIGHT -> has ? SNAKE_RIGHT_O : SNAKE_RIGHT;
            case LEFT -> has ? SNAKE_LEFT_O : SNAKE_LEFT;
            case UP -> has ? SNAKE_UP_O : SNAKE_UP;
            case DOWN -> has ? SNAKE_DOWN_O : SNAKE_DOWN;
        };
        List<Segment> segments = snek.getSegments();
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            byte dir = getStoredDirection(segment.getX(), segment.getY());
            byte lastDir = segment.getLastDirection();
            byte spr;
            if (i == segments.size() - 1) {
                spr = switch (dir) {
                    default -> TAIL_RIGHT;
                    case RIGHT -> TAIL_RIGHT;
                    case LEFT -> TAIL_LEFT;
                    case UP -> TAIL_UP;
                    case DOWN -> TAIL_DOWN;
                };
            } else if (dir == lastDir) {
                spr = switch (dir) {
                    default -> SEG_RIGHT;
                    case RIGHT -> SEG_RIGHT;
                    case LEFT -> SEG_LEFT;
                    case UP -> SEG_UP;
                    case DOWN -> SEG_DOWN;
                };
            } else {
                spr = switch (dir) {
                    default -> SEG_RIGHT;
                    case RIGHT -> lastDir == UP ? SEG_DOWN_RIGHT : SEG_UP_RIGHT;
                    case LEFT -> lastDir == UP ? SEG_DOWN_LEFT : SEG_UP_LEFT;
                    case UP -> lastDir == RIGHT ? SEG_UP_LEFT : SEG_UP_RIGHT;
                    case DOWN -> lastDir == RIGHT ? SEG_DOWN_LEFT : SEG_DOWN_RIGHT;
                };
            }
            board[segment.getX()][segment.getY()] = spr;
        }
        for (Point pt : points) board[pt.getX()][pt.getY()] = POINT;
        for (int i = 0; i < walls.length; i++)
            for (int j = 0; j < walls[0].length; j++) if (walls[i][j] != 0) board[i][j] = WALL;
        return board;
    }

    public int getHeight() {
        return height;
    }

    public List<GameListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public byte[][] getMaze() {
        return walls;
    }

    public Snake getSnake() {
        return snek;
    }

    public byte getStoredDirection(int x, int y) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        byte dir = directions[x % width][y % height];
        return dir == 0 ? RIGHT : dir;
    }

    public int getWidth() {
        return width;
    }

    public boolean hasPointAt(int x, int y) {
        return points.contains(new Point(x, y));
    }

    public boolean isFree(int x, int y) {
        if (hasPointAt(x, y) || x == snek.getX() && y == snek.getY()) return false;
        for (Segment s : snek.getSegments()) if (s.getX() == x && s.getY() == y) return false;
        if (walls[x][y] != 0) return false;
        return true;
    }

    public void moveGame() {
        directions[snek.getX()][snek.getY()] = snek.getDirection();
        switch (snek.getDirection()) {
            default:
            case RIGHT:
            case LEFT: {
                snek.setX((snek.getX() + (snek.getDirection() == RIGHT ? 1 : -1)) % width);
                if (snek.getX() < 0) snek.setX(width - 1);
                break;
            }
            case DOWN:
            case UP: {
                snek.setY((snek.getY() + (snek.getDirection() == DOWN ? 1 : -1)) % height);
                if (snek.getY() < 0) snek.setY(height - 1);
                break;
            }
        }
        snek.moveAllSegments();
        snek.setDirectionChanged(false);
        snek.setMovedDirection(snek.getDirection());
        Point point = null;
        for (Point pt : points) {
            if (pt.getX() == snek.getX() && pt.getY() == snek.getY()) {
                point = pt;
                break;
            }
        }
        if (point != null) {
            boolean c = false;
            for (GameListener ls : listeners) c = ls.collectedPoint(c);
            if (c) points.remove(point);
        }
        for (Segment pt : snek.getSegments()) if (pt.getX() == snek.getX() && pt.getY() == snek.getY()) {
            for (GameListener ls : listeners) ls.crashedIntoTail();
            return;
        }
        if (walls[snek.getX()][snek.getY()] != 0) {
            for (GameListener ls : listeners) ls.crashedIntoWall();
            return;
        }
    }

    public void putPoint(int x, int y) {
        x %= width;
        y %= height;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        Point pt = new Point(x, y);
        if (!points.contains(pt)) points.add(pt);
    }

    public void putRandomPoint() {
        int x, y;
        do {
            x = rand.nextInt(width);
            y = rand.nextInt(height);
        } while (!isFree(x, y));
        putPoint(x, y);
    }

    public boolean removeListener(GameListener listener) {
        return listeners.remove(listener);
    }

    public void setMaze(byte[][] maze) {
        if (maze.length != width || maze[0].length != height)
            throw new IllegalArgumentException("maze width and height must be equal to board size");
        walls = maze;
    }

    public void storeDirection(int x, int y, byte dir) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        directions[x % width][y % height] = dir;
    }

    private static byte[][] initByteField(int width, int height) {
        byte[][] board = new byte[width][height];
        for (byte[] element : board) {
            byte[] col = element;
            for (int y = 0; y < col.length; y++) col[y] = 0;
        }
        return board;
    }
}
