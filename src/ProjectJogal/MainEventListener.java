package ProjectJogal;

import Texture.TextureReader;
import com.sun.opengl.util.GLUT;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_CURRENT_BIT;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MainEventListener implements GLEventListener, KeyListener, MouseListener {
    int chickenIndex = 0;
    int startMenuIndex = 10;
    int RepeatCounter = 0;
    int gameOverIndex = 8;
    int NumberChicken = 5;
    int maxWidth = 100;
    int maxHeight = 100;
    int[] x = {5, 25, 45, 65, 85};
    int[] y = {70, 70, 70, 70, 70};
    boolean gameStarted = false;
    int score = 0;
    int CountNum = 0;
    int Speed = 100;
    int BasketX = 50;
    int BasketY = 5;
    int basketWidth = 10;
    int health = 3;
    int heartIndex = 7;

    String assetsFolderName = "Assets";
    String textureNames[] = {"Chicken1.png", "Chicken2.png", "Basket.png", "Treebranch.png", "Egg1.png", "Egg2.png", "Egg3.png", "Health.png","gameover.png", "Background2.png","Intro.png","Background1.png", "StartButton.png","Background2.png","instructions.png","exit.png","Score.png","Background2.png"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];
    int BasketIndex = 2;
    private List<Egg> Eggs = new ArrayList<>();
    List<Egg> Remove = new ArrayList<>();
    private Clip backgroundMusic;
    private Clip gameOverMusic;
    private boolean isStartButtonClicked = false;
    private int startButtonIndex =12;
    int instructionButtonIndex = 14;  
    int exitButtonIndex = 15; 
    int ScoreIndex = 16;
    private int startButtonX = 40;
    private int startButtonWidth = 20;
    private int startButtonHeight = 10;
    private int instructionButtonX = 70;
    private int exitButtonX = 10;
    private int buttonWidth = 20;
    private int buttonHeight = 10;
    private int buttonSpacing = 5; 
    private int startMenuCenterX = 50; 
    private int startButtonY = 60;
    private int instructionButtonY = 40;
    private int exitButtonY = 20;

    // Calculate X position to center the buttons
    private int buttonsX = 45;
    
    
    
    int Basket2X = 70; // Initial position of Player 2's basket
int Basket2Y = 5;
int basket2Width = 10;
boolean player2Active = false;

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // This will clear the background color to white

        gl.glEnable(GL.GL_TEXTURE_2D); // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Image data
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/" + "Sound" + "/ChickenSound.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        glAutoDrawable.addMouseListener(this);
    }
@Override
public void display(GLAutoDrawable glAutoDrawable) {
    GL gl = glAutoDrawable.getGL();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    gl.glLoadIdentity();
    handleKeyPress();

    if (Eggs.isEmpty()) {
        addEgg();
    }

    if (!gameStarted) {
        DrawStartMenu(gl);
    } else {
        DrawBackground(gl);
        TypeText(gl);
        if (health > 0) {
            for (Egg egg : Eggs) {
                DrawSprite(gl, egg.x, egg.y, egg.index, 3, 0);
                egg.y--;

                if (egg.y < 0) {
                    Remove.add(egg);
                    health--;
                }

                double dist = sqrdDistance(egg.x, egg.y, BasketX, BasketY);
                double radii = Math.pow(2 * 0.03 * maxHeight, 2);
                boolean isCollided = dist <= radii;

                if (isCollided) {
                    Remove.add(egg);
                    score++;
                      if (score%10==0 && Speed > 20) {
                        Speed -= 20;
                    }
                }

                if (player2Active) {
                    double dist2 = sqrdDistance(egg.x, egg.y, Basket2X, Basket2Y);
                    boolean isCollided2 = dist2 <= radii;

                    if (isCollided2) {
                        Remove.add(egg);
                        score++;
                          if (score%10==0 && Speed > 20) {
                        Speed -= 20;
                    }
                    }
                }
            }

            Eggs.removeAll(Remove);
            CountNum++;
            if (CountNum >= Speed) {
                addEgg();
                CountNum = 0;
            }

            DrawSprite(gl, BasketX, BasketY, BasketIndex, 2, 0); // Player 1's basket

            if (player2Active) {
                DrawSprite(gl, Basket2X, Basket2Y, BasketIndex, 2, 0); // Player 2's basket
            }

            DrawHealth(gl);
            RepeatCounter++;
            if (RepeatCounter >= 5) {
                chickenIndex++;
                chickenIndex = chickenIndex % 2;
                RepeatCounter = 0;
            }

            for (int i = 0; i < NumberChicken; i++) {
                DrawSprite(gl, x[i], y[i], chickenIndex, 2, 0);
            }
        } else {
            DrawGameOver(gl);
            stopBackgroundMusic();
        }
    }
}

    
     public void TypeText(GL gl) {
        gl.glPushAttrib(GL_CURRENT_BIT);
        gl.glColor4f(0f, 0f, 0f, 1.0f);
        GLUT glut = new GLUT();
        gl.glRasterPos2d(.13, 0.93);
        
        glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24,"" + score);
        gl.glPopAttrib();
         DrawSprite(gl, 45, 91, ScoreIndex,2 , 0);
    }
    
    
    
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void DrawStartMenu(GL gl) {
        gl.glEnable(GL.GL_BLEND);
       
        DrawSprite(gl,45 ,45, startMenuIndex, 10, 0);
        DrawSprite(gl, buttonsX, startButtonY, startButtonIndex, 1.5, 0);
        DrawSprite(gl, buttonsX, instructionButtonY, instructionButtonIndex, 2, 0);
        DrawSprite(gl, buttonsX, exitButtonY, exitButtonIndex, 1.4, 0);
 
        gl.glDisable(GL.GL_BLEND);
    }

    public void DrawHealth(GL gl) {
        for (int i = 0; i < health; i++) {
            DrawSprite(gl,  i * 5, 90, heartIndex, 1, 0);
        }
    }

    public void DrawGameOver(GL gl) {
        DrawSprite(gl, 45, 60, gameOverIndex, 8, 0);

        stopBackgroundMusic();
        if (gameOverMusic == null) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/" + "Sound" + "/gameover.wav"));
                gameOverMusic = AudioSystem.getClip();
                gameOverMusic.open(audioStream);
                gameOverMusic.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public double sqrdDistance(int x, int y, int x1, int y1) {
        return Math.pow(x - x1, 2) + Math.pow(y - y1, 2);
    }

    public void addEgg() {
        int characterIndex = (int) (Math.random() * NumberChicken);
        int EggX = x[characterIndex];
        int EggY = y[characterIndex];
        int index = (int) (Math.random() * 3) + 4;
        Eggs.add(new Egg(EggX, EggY, index));
    }

    public void DrawTreebranch(GL gl, int x, int y, int index, double scale, double angle) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);

        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(2.0 * scale, 0.1 * scale, 1);
        gl.glRotated(angle, 0, 0, 1);

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public void DrawSprite(GL gl, int x, int y, int index, double scale, double angle) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);

        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.1 * scale, 0.1 * scale, 1);
        gl.glRotated(angle, 0, 0, 1);

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length - 1]);

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }
    private void showInstructions() {
        // Implement this method to show game instructions
        System.out.println("Showing instructions...");
        // You might want to set a flag to display an instructions screen
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        // Handle reshape if needed
    }

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean modeChanged, boolean deviceChanged) {
        // Handle display change if needed
    }

    public void handleKeyPress() {
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            BasketX -= 2;
            if (BasketX < 0) {
                BasketX = 0;
            }
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            BasketX += 2;
            if (BasketX > maxWidth - basketWidth) {
                BasketX = maxWidth - basketWidth;
            }
        }
    }

    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyPressed(final KeyEvent e) {
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (!gameStarted) {
                    gameStarted = true;
                }
                break;
                
                
//            case KeyEvent.VK_LEFT:
//                BasketX -= 2;
//                if (BasketX < 0) {
//                    BasketX = 0;
//                }
//                break;
//            case KeyEvent.VK_RIGHT:
//                BasketX += 2;
//                if (BasketX > maxWidth - basketWidth) {
//                    BasketX = maxWidth - basketWidth;
//                }
//                break;
                
                 case KeyEvent.VK_ENTER:
            player2Active = true; // Activate Player 2
            break;
            
            case KeyEvent.VK_R:
                if (health <= 0) {
                    restartGame();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (gameStarted) {
                    backToMenu();
                }
                
                 case KeyEvent.VK_A:
            if (player2Active) {
                Basket2X -= 2;
                if (Basket2X < 0) {
                    Basket2X = 0;
                }
            }
            break;
        case KeyEvent.VK_D:
            if (player2Active) {
                Basket2X += 2;
                if (Basket2X > maxWidth - basket2Width) {
                    Basket2X = maxWidth - basket2Width;
                }
            }
            break;
            default:
               
        }
              keyBits.set(e.getKeyCode());
    }

   private void restartGame() {
    gameStarted = true;
    score = 0;
    health = 3;
    Speed = 100;
    BasketX = 50;
    Basket2X = 70; // Reset Player 2 position
    player2Active = false; // Deactivate Player 2 on restart
    Eggs.clear();
    if (gameOverMusic != null) {
        gameOverMusic.stop();
        gameOverMusic = null;
    }
    if (backgroundMusic != null && !backgroundMusic.isRunning()) {
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
}

private void backToMenu() {
    gameStarted = false;
    score = 0;
    health = 3;
    Speed = 100;
    BasketX = 50;
    Basket2X = 70; // Reset Player 2 position
    player2Active = false; // Deactivate Player 2 when returning to menu
    Eggs.clear();
    if (gameOverMusic != null) {
        gameOverMusic.stop();
        gameOverMusic = null;
    }
    if (backgroundMusic != null && !backgroundMusic.isRunning()) {
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
}

    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        // don't care
    }

    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameStarted) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            double gameX = (mouseX / (double) e.getComponent().getWidth()) * maxWidth;
            double gameY = maxHeight - (mouseY / (double) e.getComponent().getHeight()) * maxHeight;

            if (gameX >= buttonsX && gameX <= buttonsX + buttonWidth) {
                if (gameY >= startButtonY && gameY <= startButtonY + buttonHeight) {
                    gameStarted = true;
                    System.out.println("Game started!");
                } else if (gameY >= instructionButtonY && gameY <= instructionButtonY + buttonHeight) {
                    showInstructions();
                } else if (gameY >= exitButtonY && gameY <= exitButtonY + buttonHeight) {
                    System.exit(0);
                }
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}