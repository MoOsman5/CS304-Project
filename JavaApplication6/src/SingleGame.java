import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

public class SingleGame extends JFrame implements GLEventListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int FPS = 60;

    private float playerX = 0;
    private ArrayList<Letter> letters = new ArrayList<>();
    private int health = 100;
    private boolean gameOver = false;
    private Random random = new Random();
    private GLU glu = new GLU();
    private Texture backgroundTexture;
    private boolean useBackground = false;

    private static class Letter {
        char character;
        float x, y;

        Letter(char c, float x, float y) {
            this.character = c;
            this.x = x;
            this.y = y;
        }
    }

    private static final byte[][] FONT_8_BY_13 = new byte[96][];
    static {
        for (int i = 0; i < 96; i++) {
            FONT_8_BY_13[i] = new byte[13];
            for (int j = 0; j < 13; j++) {
                FONT_8_BY_13[i][j] = (byte) 0xFF;
            }
        }
    }
    static GLCanvas canvas;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the game and canvas
            canvas = new GLCanvas();
            SingleGame game = new SingleGame();
            canvas.addGLEventListener(game);
            canvas.addKeyListener(game);
            canvas.setFocusable(true);

            // Create the frame
            JFrame frame = new JFrame("Winner Winner Chicken Dinner");
            frame.setLayout(new BorderLayout());
            frame.setSize(WIDTH, HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create a panel for buttons
            JPanel controlPanel = new JPanel();
            JButton startButton = new JButton("Start");
            JButton stopButton = new JButton("Stop");
            JButton resetButton = new JButton("Reset");
            controlPanel.add(startButton);
            controlPanel.add(stopButton);
            controlPanel.add(resetButton);

            // Add canvas and buttons to frame
            frame.getContentPane().add(canvas, BorderLayout.CENTER);
            frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);

            // Setup the animator
            FPSAnimator animator = new FPSAnimator(canvas, FPS);

            // Add action listeners for start, stop, and reset buttons
            startButton.addActionListener(e -> {
                if (!animator.isAnimating()) {
                    animator.start();
                    canvas.requestFocus();
                }
            });

            stopButton.addActionListener(e -> {
                if (animator.isAnimating()) {
                    animator.stop();
                }
            });

            resetButton.addActionListener(e -> {
                game.restartGame();  // Reset game state
                if (!animator.isAnimating()) {
                    animator.start();  // Ensure the game resumes after reset
                    canvas.requestFocus();
                }
            });

            // Show the frame
            frame.setVisible(true);
        });
    }

    private Texture playerTexture;
    private Map<Character, Texture> alphabetTextures = new HashMap<>();

    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1, 1, -1, 1, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        try {
            // Load background texture
            BufferedImage backgroundImg = ImageIO.read(getClass().getClassLoader().getResource("Assets/BG2.png"));
            backgroundTexture = TextureIO.newTexture(backgroundImg, true);
            useBackground = true;

            // Load player texture
            BufferedImage playerImg = ImageIO.read(getClass().getClassLoader().getResource("Assets/basket.png"));
            playerTexture = TextureIO.newTexture(playerImg, true);

            // Load egg textures instead of alphabet textures
            Map<Character, String> eggMap = new HashMap<>();
            eggMap.put('A', "redEgg.png");
            eggMap.put('B', "goldEgg.png");
            eggMap.put('C', "whiteEgg.png");
            // Add more mappings for each letter as needed

            for (Map.Entry<Character, String> entry : eggMap.entrySet()) {
                String filename = "assets/" + entry.getValue();
                BufferedImage letterImg = ImageIO.read(getClass().getClassLoader().getResource(filename));
                alphabetTextures.put(entry.getKey(), TextureIO.newTexture(letterImg, true));
            }
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        if (gameOver) {
            checkGameOver(gl);
            return;
        }

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (useBackground) {
            drawBackground(gl);
        }
        drawPlayer(gl);
        updateAndDrawLetters(gl);
        drawHealthBar(gl);
        checkGameOver(gl);
    }

    private void checkGameOver(GL gl) {
        if (health <= 0) {
            gameOver = true;
            drawGameOverMessage(gl);
        }
    }

    private void drawGameOverMessage(GL gl) {
        String message = "GAME OVER";
        float startX = -0.6f;
        float startY = 0.0f;
        float letterSpacing = 0.1f;

        gl.glPushMatrix();
        gl.glTranslatef(startX, startY, 0.0f);

        for (char c : message.toCharArray()) {
            if (c == ' ') {
                gl.glTranslatef(letterSpacing, 0, 0);
                continue;
            }

            if (alphabetTextures.containsKey(c)) {
                Texture letterTexture = alphabetTextures.get(c);

                gl.glPushMatrix();
                gl.glTranslatef(0, 0, 0);

                gl.glEnable(GL.GL_TEXTURE_2D);
                letterTexture.bind();
                gl.glRotatef(180.0f, 0, 0, 1.0f);
                gl.glRotatef(180.0f, 0, 1.0f, 0.0f);

                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0, 0);
                gl.glVertex2f(-0.05f, -0.05f);
                gl.glTexCoord2f(1, 0);
                gl.glVertex2f(0.05f, -0.05f);
                gl.glTexCoord2f(1, 1);
                gl.glVertex2f(0.05f, 0.05f);
                gl.glTexCoord2f(0, 1);
                gl.glVertex2f(-0.05f, 0.05f);
                gl.glEnd();

                gl.glDisable(GL.GL_TEXTURE_2D);

                gl.glPopMatrix();
            }

            gl.glTranslatef(letterSpacing, 0, 0);
        }

        gl.glPopMatrix();
    }

    private void drawBackground(GL gl) {
        if (backgroundTexture != null) {
            gl.glEnable(GL.GL_TEXTURE_2D);

            backgroundTexture.bind();
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(-1, -1);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(1, -1);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(1, 1);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(-1, 1);
            gl.glEnd();
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }

    private void drawPlayer(GL gl) {
        if (playerTexture != null) {
            gl.glPushMatrix();
            gl.glTranslatef(playerX, -0.9f, 0.0f);
            gl.glRotatef(180.0f, 0, 0, 1.0f);
            gl.glScalef(10,1,50);
            gl.glEnable(GL.GL_TEXTURE_2D);
            playerTexture.bind();

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(-0.1f, -0.1f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(0.1f, -0.1f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(0.1f, 0.1f);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(-0.1f, 0.1f);
            gl.glEnd();


            // Disable texture
            gl.glDisable(GL.GL_TEXTURE_2D);

            gl.glPopMatrix();
        }
    }




    private void updateAndDrawLetters(GL gl) {
        // Generate new letters
        if (random.nextFloat() < 0.02) { // 2% chance each frame
            char c = (char) ('A' + random.nextInt(26));
            float x = random.nextFloat() * 2 - 1; // Random x between -1 and 1
            letters.add(new Letter(c, x, 1.1f));
        }

        // Update and draw letters
        for (int i = letters.size() - 1; i >= 0; i--) {
            Letter letter = letters.get(i);
            letter.y -= 0.005f; // Slow down falling speed by reducing this value (was 0.01f)

            // Draw letter image
            if (alphabetTextures.containsKey(letter.character)) {
                gl.glPushMatrix();
                gl.glTranslatef(letter.x, letter.y, 0.0f);
                gl.glRotatef(180.0f,0,0,1.0f);
                gl.glRotatef(180.0f,0,1.0f,0.0f);

                Texture letterTexture = alphabetTextures.get(letter.character);
                letterTexture.bind();

                gl.glEnable(GL.GL_TEXTURE_2D);
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0, 0); gl.glVertex2f(-0.05f, -0.05f); // Bottom-left
                gl.glTexCoord2f(1, 0); gl.glVertex2f(0.05f, -0.05f);  // Bottom-right
                gl.glTexCoord2f(1, 1); gl.glVertex2f(0.05f, 0.05f);   // Top-right
                gl.glTexCoord2f(0, 1); gl.glVertex2f(-0.05f, 0.05f);  // Top-left
                gl.glEnd();
                gl.glDisable(GL.GL_TEXTURE_2D);

                gl.glPopMatrix();
            }

            // Check if letter hit bottom
            if (letter.y < -1.0f) {
                letters.remove(i);
                health -= 10; // Decrease health
            }
        }
    }



    private void drawHealthBar(GL gl) {
        float healthWidth = (float) health / 100 * 0.5f;
        gl.glPushMatrix();
        gl.glTranslatef(-0.9f, 0.9f, 0.0f);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0.0f, -0.1f);
        gl.glVertex2f(healthWidth, -0.1f);
        gl.glVertex2f(healthWidth, 0.05f);
        gl.glVertex2f(0.0f, 0.05f);
        gl.glEnd();
        gl.glPopMatrix();
    }



    private void drawString(GL gl, String string) {
        gl.glPointSize(20.0f);
        gl.glBegin(GL.GL_POINTS);
        float x = 0;
        float y = 0;
        for (char c : string.toCharArray()) {
            if (c < 32 || c >= 128) continue;  // Skip non-printable characters
            byte[] charData = FONT_8_BY_13[c - 32];
            for (int row = 0; row < 13; row++) {
                for (int col = 0; col < 8; col++) {
                    if ((charData[row] & (1 << (7 - col))) != 0) {
                        gl.glVertex2f(x + col, y - row);
                    }
                }
            }
            x += 10;  // Move to the next character position
        }
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        // This method is part of the older JOGL API
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                playerX = Math.max(playerX - 0.1f, -1.0f);
                break;
            case KeyEvent.VK_RIGHT:
                playerX = Math.min(playerX + 0.1f, 1.0f);
                break;
            case KeyEvent.VK_R:
                if (gameOver) {
                    restartGame();
                }
                break;
            default:
                // Handle letter key presses
                char keyChar = Character.toUpperCase(e.getKeyChar());
                if (keyChar >= 'A' && keyChar <= 'Z') {
                    removeLetter(keyChar);  // Call the function to remove the letter
                }
        }
    }

    private void removeLetter(char c) {
        for (int i = letters.size() - 1; i >= 0; i--) {
            if (letters.get(i).character == c) {
                letters.remove(i);  // Remove the letter from the list
                break;  // Stop after removing the first occurrence
            }
        }
    }


    private void restartGame() {
        health = 100;
        gameOver = false;
        letters.clear();
        playerX = 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}