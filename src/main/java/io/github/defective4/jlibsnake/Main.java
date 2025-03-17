package io.github.defective4.jlibsnake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import io.github.defective4.jlibsnake.event.GameAdapter;
import io.github.defective4.jlibsnake.sprite.BitSheet;
import io.github.defective4.jlibsnake.sprite.Snake;

public class Main {
    public static void main(String[] args) {
        BitSheet sheet;
        try (InputStream is = Main.class.getResourceAsStream("/sheet.png")) {
            sheet = new BitSheet(ImageIO.read(is));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        LibSnake snake = new LibSnake(24, 24);
        snake.putRandomPoint();
        snake.addListener(new GameAdapter() {
            @Override
            public boolean collectedPoint(boolean consumePoint) {
                snake.putRandomPoint();
                snake.getSnake().appendSegment();
                return true;
            }
        });

        JFrame win = new JFrame("Snake");
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel snakePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                byte[][] board = snake.getByteField();
                for (int x = 0; x < board.length; x++) {
                    byte[] col = board[x];
                    for (int y = 0; y < col.length; y++) {
                        byte sprIx = col[y];
                        if (sprIx == 0) {
                            g.setColor(Color.white);
                            g.fillRect(x * 16, y * 16, 16, 16);
                            continue;
                        }
                        BufferedImage spr;
                        spr = BitSheet.toImage(sheet.getSpriteFor(sprIx));
                        g.drawImage(spr, x * 16, y * 16, 16, 16, null);
                    }
                }
            }
        };
        snakePanel.setPreferredSize(new Dimension(24 * 16, 24 * 16));
        win.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                byte dir = switch (e.getKeyCode()) {
                    default -> -1;
                    case KeyEvent.VK_LEFT -> Snake.LEFT;
                    case KeyEvent.VK_RIGHT -> Snake.RIGHT;
                    case KeyEvent.VK_DOWN -> Snake.DOWN;
                    case KeyEvent.VK_UP -> Snake.UP;
                };
                if (dir != -1) snake.getSnake().setDirection(dir);
            }
        });

        win.setContentPane(snakePanel);
        win.pack();
        win.setResizable(false);
        win.setVisible(true);
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snake.moveGame();
                snakePanel.repaint();
            }
        }, 200, 200);
    }
}
