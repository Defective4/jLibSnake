package io.github.defective4.jlibsnake.sprite;

import static io.github.defective4.jlibsnake.sprite.Sprites.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class BitSheet {
    private final byte[][] map;
    private final Map<Byte, byte[][]> spriteMap = new HashMap<>();

    public BitSheet(BufferedImage spriteSheet) {
        map = new byte[spriteSheet.getWidth()][spriteSheet.getHeight()];
        int black = Color.black.getRGB();
        for (int x = 0; x < spriteSheet.getWidth(); x++) for (int y = 0; y < spriteSheet.getHeight(); y++) {
            map[x][y] = (byte) (spriteSheet.getRGB(x, y) == black ? 1 : 0);
        }

        spriteMap.put(SNAKE_LEFT, getSpriteAt(0, 2));
        spriteMap.put(SNAKE_RIGHT, getSpriteAt(2, 3));
        spriteMap.put(SNAKE_UP, getSpriteAt(3, 2));
        spriteMap.put(SNAKE_DOWN, getSpriteAt(4, 3));

        spriteMap.put(SNAKE_LEFT_O, getSpriteAt(10, 0));
        spriteMap.put(SNAKE_RIGHT_O, getSpriteAt(10, 1));
        spriteMap.put(SNAKE_UP_O, getSpriteAt(10, 3));
        spriteMap.put(SNAKE_DOWN_O, getSpriteAt(10, 2));

        spriteMap.put(SEG_LEFT, getSpriteAt(1, 2));
        spriteMap.put(SEG_RIGHT, getSpriteAt(1, 3));
        spriteMap.put(SEG_UP, getSpriteAt(3, 3));
        spriteMap.put(SEG_DOWN, getSpriteAt(4, 2));

        spriteMap.put(SEG_DOWN_RIGHT, getSpriteAt(6, 2));
        spriteMap.put(SEG_DOWN_LEFT, getSpriteAt(7, 2));
        spriteMap.put(SEG_UP_RIGHT, getSpriteAt(6, 3));
        spriteMap.put(SEG_UP_LEFT, getSpriteAt(7, 3));

        spriteMap.put(TAIL_RIGHT, getSpriteAt(0, 3));
        spriteMap.put(TAIL_LEFT, getSpriteAt(2, 2));
        spriteMap.put(TAIL_UP, getSpriteAt(9, 2));
        spriteMap.put(TAIL_DOWN, getSpriteAt(8, 2));

        spriteMap.put(POINT, getSpriteAt(5, 2));
    }

    public void dump(File pngFile) throws IOException {
        ImageIO.write(toImage(map), "png", pngFile);
    }

    public byte[][] getSpriteFor(byte index) {
        if (!spriteMap.containsKey(index)) throw new IllegalArgumentException("Invalid sprite index");
        return spriteMap.get(index);
    }

    private byte[][] getSpriteAt(int sqX, int sqY) {
        byte[][] sprite = new byte[4][4];
        for (int x = 0; x < 4; x++) for (int y = 0; y < 4; y++) sprite[x][y] = map[sqX * 4 + x][sqY * 4 + y];
        return sprite;
    }

    public static BufferedImage toImage(byte[][] sprite) {
        BufferedImage img = new BufferedImage(sprite.length, sprite[0].length, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < img.getWidth(); x++) for (int y = 0; y < img.getHeight(); y++)
            img.setRGB(x, y, (sprite[x][y] > 0 ? Color.black : Color.white).getRGB());
        return img;
    }
}
