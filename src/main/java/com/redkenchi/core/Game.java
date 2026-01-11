package com.redkenchi.core;

import com.redkenchi.core.graphics.Screen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {

    public static final Logger logger = LogManager.getLogger("Apocalyptic World");
    public static final String TITLE = "Apocalyptic World";
    public static final double FPS = 60;
    public static final int WIDTH = 300;
    public static final int HEIGHT = WIDTH / 16 * 9;
    public static final int SCALE = 3;
    public static boolean running;
    private Thread thread;
    Screen screen;
    public BufferedImage image = new BufferedImage(WIDTH * SCALE, HEIGHT * SCALE, BufferedImage.TYPE_INT_RGB);
    public int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public Game() {
        this.setSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        screen = new Screen(this.getWidth(), this.getHeight());
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this, "core");
        logger.info("Starting Game Thread");
        thread.start();
    }
    public synchronized void stop() {
        running = false;
    }

    @Override
    public void run() {
        double lastTime = System.nanoTime();
        double lastMillis = System.currentTimeMillis();
        double nsPerUpdate = 1000000000.0/FPS;
        int ticks = 0;
        int frames = 0;
        double delta = 0;

        while (running) {
            double now = System.nanoTime();
            double currentMillis = System.currentTimeMillis();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

            while (delta >= 1) {
                tick();
                ticks++;
                delta = 0;
            }
            {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                frames++;
                render();
            }

            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = i + frames;
            }

            if (currentMillis - lastMillis >= 1000) {
                System.out.println("ticks: " + ticks + " fps: " + frames);
                ticks = 0;
                frames = 0;
                lastMillis += 1000;
            }
        }
    }
    public void tick () {
    }
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        //g.setColor(Color.BLACK);
        //g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        
        g.dispose();
        bs.show();

    }

    public static void main(String[] args) {
        logger.info("Starting com.redkenchi.core.Game");
        JFrame frame = new JFrame(Game.TITLE);
        Game game = new Game();
        frame.add(game);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        game.start();
    }
}
