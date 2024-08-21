package ProjectJogal;

import Texture.TextureReader;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class MainEventListener implements GLEventListener, KeyListener {
    int chickenIndex = 0;  
    int RepeatCounter = 0;
    int NumberChicken = 5;
    int maxWidth = 100;
    int maxHeight = 100;
    int[] x = {5, 25, 45, 65, 85};
    int[] y = {70, 69, 69, 70, 70};
    int CountNum = 0;
    int Speed = 100; 
    int BasketX = 50;  
    int BasketY = 15;
    int basketWidth = 10; 
    String assetsFolderName = "Assets";
    String textureNames[] = {"Chicken1.png", "Chicken2.png", "Basket.png", "Treebranch.png", "Egg1.png", "Egg2.png", "Egg3.png", "Health.png", "Background2.png"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];
    int BasketIndex = 2;
    private List<Egg> Eggs = new ArrayList<>();
     List<Egg> Remove = new ArrayList<>();
    
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
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       
        gl.glLoadIdentity(); 
        DrawBackground(gl);
        
        if(Eggs.isEmpty()){
      addEgg();
        }
        // Chicken
        RepeatCounter++;
        if (RepeatCounter >= 5) {
            chickenIndex++;
            chickenIndex = chickenIndex % 2;
            RepeatCounter = 0;
        }
        
        // Draw chicken
        for (int i = 0; i < NumberChicken; i++) {
            DrawSprite(gl, x[i], y[i], chickenIndex, 2, 0);   
        }
        
        // Draw tree branch
        DrawTreebranch(gl, 0, 61, 3, 1.0, 0);
    
        // Draw eggs
        for (int i = 0; i < Eggs.size(); i++) {
            Egg egg = Eggs.get(i);
            DrawSprite(gl, egg.x, egg.y, egg.index, 3, 0);
            egg.y--;
            
                if (egg.y < 0) {
                    Speed-=5;
                   Remove.add(egg);
                }
        }
        
        Eggs.removeAll(Remove);
      
        CountNum++;
        if (CountNum >=Speed ) {
            
            addEgg();
            CountNum = 0;
        }
          System.out.println(CountNum+"   "+ Speed);
  
     
        DrawSprite(gl, BasketX, BasketY, BasketIndex, 2, 0);
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
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length - 1]); // Turn Blending On

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);
        // Front Face
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

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        // Handle reshape if needed
    }

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean modeChanged, boolean deviceChanged) {
        // Handle display change if needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            BasketX -= 1;
            if (BasketX < 0) {
                BasketX = 0; 
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            BasketX += 1;
            if (BasketX > maxWidth - basketWidth) {
                BasketX = maxWidth - basketWidth; 
            }
        } else if (key == KeyEvent.VK_E) { // على سبيل المثال، اضغط على 'E' لإضافة بيضة
            addEgg();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key release if needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Handle key typed if needed
    }

    public static void main(String[] args) {
        // Initialize your application
    }

    
}
